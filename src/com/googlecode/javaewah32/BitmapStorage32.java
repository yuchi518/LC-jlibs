package com.googlecode.javaewah32;

/*
 * Copyright 2009-2014, Daniel Lemire, Cliff Moon, David McIntosh, Robert Becho, Google Inc., Veronika Zenz, Owen Kaser, Gregory Ssi-Yan-Kai, Rory Graves
 * Licensed under the Apache License, Version 2.0.
 */

/**
 * Low level bitset writing methods.
 *
 * @author Daniel Lemire and David McIntosh
 * @since 0.5.0
 */
public interface BitmapStorage32 {

    /**
     * Adding words directly to the bitmap (for expert use).
     * 
     * This is normally how you add data to the array. So you add bits in
     * streams of 8*8 bits.
     *
     * @param newData the word
     */
    void addWord(final int newData);

    /**
     * if you have several literal words to copy over, this might be faster.
     *
     * @param data   the literal words
     * @param start  the starting point in the array
     * @param number the number of literal words to add
     */
    void addStreamOfLiteralWords(final int[] data, final int start,
                                        final int number);

    /**
     * For experts: You want to add many zeroes or ones? This is the method
     * you use.
     *
     * @param v      zeros or ones
     * @param number how many to words add
     */
    void addStreamOfEmptyWords(final boolean v, final int number);

    /**
     * Like "addStreamOfLiteralWords" but negates the words being added.
     *
     * @param data   the literal words
     * @param start  the starting point in the array
     * @param number the number of literal words to add
     */
    void addStreamOfNegatedLiteralWords(int[] data, final int start,
                                               final int number);

    /**
     * Empties the container.
     */
    void clear();

    /**
     * directly set the sizeInBits field
     *
     * @param bits number of bits
     */
    void setSizeInBits(final int bits);
}
