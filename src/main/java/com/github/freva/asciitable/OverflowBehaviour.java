package com.github.freva.asciitable;

import java.util.stream.Stream;

public enum OverflowBehaviour {

    /** Break text to newline when max column width is reached */
    NEWLINE {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return LineUtils.splitTextIntoLinesOfMaxLength(paragraph, limit).stream();
        }
    },

    /** Clip text from the left when max column width is reached */
    CLIP_LEFT {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return Stream.of(paragraph.substring(paragraph.length() - limit));
        }
    },

    /** Clip text from the right text when max column width is reached */
    CLIP_RIGHT {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return Stream.of(paragraph.substring(0, limit));
        }
    },

    /** Clip and prepend '…' the from the left when max column width is reached */
    ELLIPSIS_LEFT {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return Stream.of(ELLIPSIS_CHAR + paragraph.substring(paragraph.length() - limit + 1));
        }
    },

    /** Clip and prepend '…' the from the right when max column width is reached */
    ELLIPSIS_RIGHT {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return Stream.of(paragraph.substring(0, limit - 1) + ELLIPSIS_CHAR);
        }
    },

    /**
     * Replace the middle of the text with '…' when max width is reached.
     * Keeps both the beginning and the end visible.
     * 
     * @since 1.10.0
     */
    ELLIPSIS_CENTER {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            int prefixLen = (limit - 1) / 2;
            int suffixLen = limit - 1 - prefixLen;
            return Stream.of(paragraph.substring(0, prefixLen) + ELLIPSIS_CHAR
                    + paragraph.substring(paragraph.length() - suffixLen));

        }
    },

    /**
     * Use {@link com.github.freva.asciitable.OverflowBehaviour#CLIP_RIGHT} instead
     */
    @Deprecated
    CLIP {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return CLIP_RIGHT.doApply(paragraph, limit);
        }
    },

    /**
     * Use {@link com.github.freva.asciitable.OverflowBehaviour#ELLIPSIS_RIGHT}
     * instead
     */
    @Deprecated
    ELLIPSIS {
        @Override
        Stream<String> doApply(String paragraph, int limit) {
            return ELLIPSIS_RIGHT.doApply(paragraph, limit);
        }
    };

    private static final char ELLIPSIS_CHAR = '…';

    /**
     * Apply the current overflow behavior into the given paragraph, if needed,
     * and return a stream of lines.
     *
     * @param paragraph the paragraph to be processed
     * @param limit the maximum column limit to be applied
     * @return a stream of lines after overflow processing
     */
    public Stream<String> apply(String paragraph, int limit) {
        return paragraph.length() <= limit ? Stream.of(paragraph) : doApply(paragraph, limit);
    }

    abstract Stream<String> doApply(String paragraph, int limit);

}
