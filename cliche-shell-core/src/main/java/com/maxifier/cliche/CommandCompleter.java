/*
* Copyright (c) 2008-2015 Maxifier Ltd. All Rights Reserved.
*/
package com.maxifier.cliche;

import java.util.List;

/**
 * CommandCompleter
 *
 * @author Igor Yankov (igor.yankov@maxifier.com) (2015-02-26 17:05)
 */
public interface CommandCompleter {
    /**
     * Populates candidates with a list of possible completions for the <i>buffer</i>.
     *
     * @param buffer The buffer
     * @return the {@link List} of candidates to populate
     */
    public List<CharSequence> complete(String buffer);
}
