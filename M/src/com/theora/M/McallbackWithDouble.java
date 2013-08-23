package com.theora.M;

/**
 * a general purpose callback with a function f passed in a Double
 * Double, not double, so that null can also be passed when necessary
 */
public interface McallbackWithDouble {
	public void f(Double d);
}