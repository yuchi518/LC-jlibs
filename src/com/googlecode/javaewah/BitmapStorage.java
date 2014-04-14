package com.googlecode.javaewah;

/*
 * Copyright 2009-2014, Daniel Lemire, Cliff Moon, David McIntosh, Robert Becho, Google Inc., Veronika Zenz, Owen Kaser, gssiyankai
 * Licensed under the Apache License, Version 2.0.
 */

/**
 * Low level bitset writing methods.
 * 
 * @since 0.4.0
 * @author David McIntosh
 */
public interface BitmapStorage {

        /**
         * Adding words directly to the bitmap (for expert use).
         * 
         * This is normally how you add data to the array. So you add bits in
         * streams of 8*8 bits.
         * 
         * @param newdata
         *                the word
         */
        public void addWord(final long newdata);

        /**
         * if you have several literal words to copy over, this might be faster.
         * 
         * @param data
         *                the literal words
         * @param start
         *                the starting point in the array
         * @param number
         *                the number of literal words to add
         */
        public void addStreamOfLiteralWords(final long[] data, final int start,
                final int number);

        /**
         * For experts: You want to add many zeroes or ones? This is the method
         * you use.
         * 
         * @param v
         *                zeros or ones
         * @param number
         *                how many to words add
         */
        public void addStreamOfEmptyWords(final boolean v, final long number);

        /**
         * Like "addStreamOfLiteralWords" but negates the words being added.
         * 
         * @param data
         *                the literal words
         * @param start
         *                the starting point in the array
         * @param number
         *                the number of literal words to add
         */
        public void addStreamOfNegatedLiteralWords(long[] data,
                final int start, final int number);
        
        /**
         * Empties the container.
         */
        public void clear();

        /**
         * directly set the sizeinbits field
         * 
         * @param bits
         *                number of bits
         */
        public void setSizeInBits(final int bits);
}
