package com.github.freva.asciitable;

public enum OverflowBehaviour {

    /** Break text to newline when max column width is reached */
    NEWLINE,

    /** Clip text from the left when max column width is reached */
    CLIP_LEFT,

    /** Clip text from the right text when max column width is reached */
    CLIP_RIGHT,

    /** Clip and prepend '…' the from the left when max column width is reached */
    ELLIPSIS_LEFT,

    /** Clip and prepend '…' the from the right when max column width is reached */
    ELLIPSIS_RIGHT,


    /** Use {@link com.github.freva.asciitable.OverflowBehaviour#CLIP_RIGHT} instead */
    @Deprecated
    CLIP,

    /** Use {@link com.github.freva.asciitable.OverflowBehaviour#ELLIPSIS_RIGHT} instead */
    @Deprecated
    ELLIPSIS
}
