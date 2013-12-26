package com.googlecode.javaewah;

/*
 * Copyright 2009-2013, Daniel Lemire, Cliff Moon, David McIntosh, Robert Becho, Google Inc., Veronika Zenz and Owen Kaser
 * Licensed under the Apache License, Version 2.0.
 */
/**
 * This is a BitmapStorage that can be used to determine quickly if the result
 * of an operation is non-trivial... that is, whether there will be at least on
 * set bit.
 * 
 * @since 0.4.2
 * @author Daniel Lemire and Veronika Zenz
 * 
 */
public class NonEmptyVirtualStorage implements BitmapStorage {
  static class NonEmptyException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Do not fill in the stack trace for this exception
     * for performance reasons.
     *
     * @return this instance
     * @see java.lang.Throwable#fillInStackTrace()
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
  }
  
  private static final NonEmptyException nonEmptyException = new NonEmptyException();

  /**
   * If the word to be added is non-zero, a NonEmptyException exception is
   * thrown.
   * 
   * @see com.googlecode.javaewah.BitmapStorage#add(long)
   */
  @Override
public void add(long newdata) {
    if (newdata != 0)
      throw nonEmptyException;
    return;
  }

  /**
   * throws a NonEmptyException exception when number is greater than 0
   * 
   */
  @Override
public void addStreamOfLiteralWords(long[] data, int start, int number) {
      if(number>0){
          throw nonEmptyException;
      }
  }

  /**
   * If the boolean value is true and number is greater than 0, then it throws a NonEmptyException exception,
   * otherwise, nothing happens.
   * 
   * @see com.googlecode.javaewah.BitmapStorage#addStreamOfEmptyWords(boolean, long)
   */
  @Override
public void addStreamOfEmptyWords(boolean v, long number) {
    if (v && (number>0))
      throw nonEmptyException;
    return;
  }

  /**
   * throws a NonEmptyException exception when number is greater than 0
   * 
   */
  @Override
public void addStreamOfNegatedLiteralWords(long[] data, int start, int number) {
      if(number>0){
          throw nonEmptyException;
      }
  }

  /**
   * Does nothing.
   * 
   * @see com.googlecode.javaewah.BitmapStorage#setSizeInBits(int)
   */
  @Override
public void setSizeInBits(int bits) {
  }

}
