<?php

/*------------------------------------------------------------*/
class Nautilus extends Mcontroller {
	/*------------------------------------------------------------*/
	private $gameNames = array(
		"",
		"Animals",
		"Actors",
		"Actresses",
		"Musicians",
	);
	private $difficultyNames = array(
		"", // difficultyNone, no folder
		"Baby",
		"Easy",
		"Medium",
		"Hard",
	);
	/*------------------------------------------------------------*/
	public function defaultAction() {
		/*	$this->sendSampleGame();	*/
		$this->sendGame();
	}
	/*------------------------------------------------------------*/
	public function sendSampleGame() {
		header ("Content-Type: text/xml");
		readfile("sample.xml");
	}
	/*------------------------------------------------------------*/
	private function listDir($dir) {
		$dir = opendir($dir);
		while($file = readdir($dir))
			if ( $file != '.' && $file != '..' && $file != 'index.php' )
				$files[] = $file;
		closedir($dir);
		return($files);
	}
	/*------------------------------------------------------------*/
	/*
	 *  get a question from fileList
	 * but be sure its not one of $questions.
	 */
	private function question($fileList, $previousQuestions, $numChoices = 4) {
		$cnt = count($fileList);
		$previousFiles = array();
		$qlen = count($previousQuestions);
		for($i=0;$i<$qlen;$i++)
			$previousFiles[] = $previousQuestions[$i]['file'];
		if ( $cnt == $qlen ) {
			echo "too few choices";
			exit;
		}
		while ( true ) {
			$answer = $fileList[rand(0, $cnt-1)];
			if ( ! in_array($answer, $previousFiles) )
				break;
		}
		$question['file'] = $answer;
		$options = array();
		for($i=1;$i<$numChoices;$i++) {
			while ( true ) {
				$option = $fileList[rand(0, $cnt-1)];
				if ( $option != $answer && ! in_array($option, $options) )
					break;
			}
			$options[] = $option;
		}
		$question['options'] = $options;
		return($question);
	}
	/*------------------------------------------------------------*/
	/**
	 * send a game
	 */
	public function sendGame() {
		$gameId = @$_REQUEST['gameId'];
		if ( ! $gameId )
			$gameId = 1;
		$difficulty = $_REQUEST['difficulty'];
		$numItems = $_REQUEST['numItems'];
		$gameName = $this->gameNames[$gameId];
		$gameDifficulty = $this->difficultyNames[$difficulty];
		$folder = "images/$gameName/$gameDifficulty";
		$fileList = $this->listDir($folder);
		$numChoices = @$_REQUEST['numChoices'];
		$isPlain = @$_REQUEST['plain'];
		if ( ! $numChoices )
			$numChoices = 4;
		$cnt = count($fileList);
		$questions = array();
		for($i=0;$i<$numItems;$i++)
			$questions[$i] = $this->question($fileList, $questions, $numChoices);

		if ( $isPlain ) {
			header ("Content-Type: text/plain");
			$text = $this->questions2plain($questions);
			echo $text;
		} else {
			header ("Content-Type: text/xml");
			$text = $this->questions2Xml($questions);
			echo $this->xmlHeader;
			echo $text;
		}
	}
	/*------------------------------------------------------------*/
	private function questions2plain($questions) {
		$ret = "";
		foreach ( $questions as $question )
			$ret .= $this->question2plain($question);
		return($ret);
	}
	/*------------------------------*/
	private function question2plain($question) {
		$ret = "";
		$file = $question['file'];
		$ret .= "$file\n";
		$ret .= $this->options2plain($question['options']);
		return($ret);
	}
	/*------------------------------*/
	private function options2plain($options) {
		$ret = "";
		foreach ( $options as $option )
			$ret .= "\t$option\n";
		return($ret);
	}
	/*------------------------------------------------------------*/
	private function questions2Xml($questions) {
		$ret = "";
		$ret .= "<questions>\n";
		foreach ( $questions as $question )
			$ret .= $this->question2Xml($question);
		$ret .= "</questions>\n";
		return($ret);
	}
	/*------------------------------*/
	private function question2Xml($question) {
		$ret = "";
		$ret .= "\t<question>\n";
		$file = $question['file'];
		$ret .= "\t\t<file>$file</file>\n";
		$ret .= $this->options2xml($question['options']);
		$ret .= "\t</question>\n";
		return($ret);
	}
	/*------------------------------*/
	private function options2xml($options) {
		$ret = "";
		$ret .= "\t\t<options>\n";
		foreach ( $options as $option )
			$ret .= "\t\t\t<option>$option</option>\n";
		$ret .= "\t\t</options>\n";
		return($ret);
	}
	/*------------------------------------------------------------*/
	// keep this at bottom so vi doesn't confuse this file as an xml file
	private $xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
?>
