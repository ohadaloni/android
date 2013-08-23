/*------------------------------------------------------------*/
package com.theora.sudoku;

import com.theora.M.Mutils;

import android.content.Context;

/*------------------------------------------------------------*/
public class SudokuMaker {
	private Context context = null;
	private Mutils mutils = null;
	private int m[][] = new int[9][9];
	private boolean logSingleSwaps = true;
	private boolean logTransformations = false;
	@SuppressWarnings("unused")
	private final String diagonalKernel = 
		"100000000020000000003000000" + "000400000000050000000006000" + "000000700000000080000000009" ;
	private final String[] easyKernels = {
			"360000000004230800000004200" + "070460003820000014500013020" + "001900000007048300000000045",
			"000091508000320006720040090" + "018003000500000009000500210" + "000000071100068050904150030",
			"020100000065038400070000003" + "300050009040020070900080001" + "500000030006240800000006010"
	};
	private final String[] mediumKernels = {
			"650000070000506000014000005" + "007009000002314700000700800" + "500000630000201000030000097",
			"000008109009004000052700003" + "006000072000060000180000500" + "300009780000400600901500000",
			"020000050105030802030508060" + "007000100050000030001000400" + "080709010206050908070000040",
			"000600010035200700028500600" + "000080102801000904209040000" + "002003500306005470070009000",
			"360000000020500704005070100" + "000009800092608030004700000" + "009087400476005090008000075",
			"000013005009000400070004030" + "000000601800060009504000000" + "040900070002000300100580000",
			"000020000006301800050000020" + "010806070400000009080509040" + "020000090005703100000090000",
			"050601070700000003000502000" + "507000301000040000803000502" + "000403000200000007060207080"
		};
	private final String[] hardKkernels = {
			"009000000080605020501078000" + "000000700706040102004000000" + "000720903090301080000000600",
			"000020000085007490061000850" + "090000000800030006000000040" + "027000580019500360000060000",
			"006000800500406002040000070" + "080605010000000000010802040" + "030000090400907006001000500",
			"002003070908000100700905000" + "020000400060304050001000080" + "000601005007000004080400600",
			"006400050500009000000520030" + "000100305300000008401007000" + "070086000000900007050004900"
		};
	private final String[][] kernels = { easyKernels, mediumKernels, hardKkernels };
	/*------------------------------------------------------------*/
	public SudokuMaker(Context ctx) {
		this.context = ctx;
		this.mutils = new Mutils(this.context);
	}
	/*------------------------------------------------------------*/
	public String make(int difficulty) {
		String[] levelKernels = kernels[difficulty];
//		String kernel = diagonalKernel;
		String kernel = levelKernels[Mutils.rand(0, levelKernels.length-1)];
		toMatrix(kernel);
		int numTransforms = 50 ;
//		int numTransforms = 0 ;
		for ( int i=0;i<numTransforms;i++)
			transform();
		return(toString());
	}
	/*------------------------------------------------------------*/
	private void toMatrix(String string) {
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				m[i][j] = string.charAt(i*9+j) - '0';
	}
	/*------------------------------*/
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				buf.append(m[i][j]);
		return buf.toString();
	}
	/*------------------------------------------------------------*/
	/*
	 * its OK to swap any two columns in the same major column
	 * i.e. two of 0,1,2 or 3,4,5 or 6,7,8
	 * this function also used for a total flip around the center col=4
	 */
	private void swapColumns(int cx, int cy) {
		if ( logTransformations && logSingleSwaps )
			mutils.log(String.format("swapColumns: %d,%d", cx, cy));
		int tmp;
		for(int i=0;i<9;i++) {
			tmp = m[i][cx];
			m[i][cx] = m[i][cy];
			m[i][cy] = tmp;
		}
	}
	/*------------------------------*/
	private void swapRows(int rx, int ry) {
		if ( logTransformations && logSingleSwaps )
			mutils.log(String.format("swapRows: %d,%d", rx, ry));
		int tmp;
		for(int i=0;i<9;i++) {
			tmp = m[rx][i];
			m[rx][i] = m[ry][i];
			m[ry][i] = tmp;
		}
	}
	/*------------------------------------------------------------*/
	private void flipHorizontal() {
		if ( logTransformations)
			mutils.log("flipHorizontal");
		swapRows(0, 8);
		swapRows(1, 7);
		swapRows(2, 6);
		swapRows(3, 5);
	}
	/*------------------------------*/
	private void flipVertical() {
		if ( logTransformations)
			mutils.log("flipVertical");
		swapColumns(0, 8);
		swapColumns(1, 7);
		swapColumns(2, 6);
		swapColumns(3, 5);
	}
	/*------------------------------------------------------------*/
	// major row 2 is the set of the three rows 6,7,8
	// swapMajorRows(0..2, 0..2)
	private void swapMajorRows(int rx, int ry) {
		if ( logTransformations)
			mutils.log(String.format("swapMajorRows: %d,%d", rx, ry));
		logSingleSwaps = false;
		for(int i=0;i<3;i++)
			swapRows(rx*3+i, ry*3+i);
	}
	/*------------------------------*/
	private void swapMajorColumns(int cx, int cy) {
		if ( logTransformations)
			mutils.log(String.format("swapMajorColumns: %d,%d", cx, cy));
		logSingleSwaps = false;
		for(int i=0;i<3;i++)
			swapColumns(cx*3+i, cy*3+i);
	}
	/*------------------------------------------------------------*/
	// alias rand m.util.rand
	private int rand(int n, int m) {
		return(Mutils.rand(n, m));
	}
	/*------------------------------*/
	public void randTest() {
		String ret = "randTest: ";
		for(int i=0;i<20;i++) {
			int r = rand(1, 10);
			ret  = ret + ", " + r;
		}
		mutils.log(ret);
	}
	/*------------------------------------------------------------*/
	private void swapMinor() {
		int section = rand(0, 2);
		int begin = section*3;
		int end = section*3 + 2;
		int m = 0;
		int n = 0;
		while ( n == m ) {
			n = rand(begin, end);
			m = rand(begin, end);
		}
		if ( rand(1,2)  == 1 )
			swapColumns(n, m);
		else
			swapRows(n, m);
	}
	/*------------------------------*/
	private void swapMajor() {
		int m = 0;
		int n = 0;
		while ( n == m ) {
			n = rand(0,2);
			m = rand(0,2);
		}
		if ( rand(1,2)  == 1 )
			swapMajorColumns(n, m);
		else
			swapMajorRows(n, m);
	}
	/*------------------------------*/
	private void swap() {
		if ( rand(1,10) < 8 )
			swapMinor();
		else
			swapMajor();
	}
	/*------------------------------*/
	private void flip() {
		if ( rand(1,2)  == 1 )
			flipHorizontal();
		else
			flipVertical();
	}
	/*------------------------------*/
	private void replace(int first, int second) {
		final int magic = 1961;
		
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if ( m[i][j] == first )
					m[i][j] = magic;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if ( m[i][j] == second )
					m[i][j] = first;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if ( m[i][j] == magic )
					m[i][j] = second;
	}
	/*------------------------------*/
	private void replace() {
		int first = rand(1, 9);
		int second;
		while ( ( second = rand(1,9)) == first )
			;
		replace(first, second);
	}
	/*------------------------------*/
	private void transform() {
		logSingleSwaps = true;
		/*	String before = toString();	*/
		int r = rand(1, 100);
		if ( r  < 20 )
			replace();
		else if ( r < 30 )
			flip();
		else
			swap();
		/*	String after = toString();	*/
		/*	mutil.log("Before: " + before);	*/
		/*	mutil.log("After: " + after);	*/
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/

