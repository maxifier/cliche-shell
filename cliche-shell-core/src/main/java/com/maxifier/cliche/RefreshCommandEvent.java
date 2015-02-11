/*
* Copyright (c) 2008-2015 Maxifier Ltd. All Rights Reserved.
*/
package com.maxifier.cliche;

import com.google.common.collect.ObjectArrays;

/**
 * RefreshCommandEvent is event which is emitted to let command-UI know
 * that if a user observes the result of a command with name from this list {@link RefreshCommandEvent#commands},
 * then his screen should be refreshed by reinvocation this command.<p/>
 * <p/>
 * It's supposed that users can observes only commands marked as {@link Command#followable()}.
 * Other Commands will ne be automatically refreshable hence they will not perceive such events.
 *
 * @author Igor Yankov (igor.yankov@maxifier.com) (2015-02-11 20:30)
 */
public class RefreshCommandEvent {
    private final String[] commands;

    public RefreshCommandEvent(String command, String... commands) {
        this.commands = ObjectArrays.concat(new String[]{command}, commands, String.class);
    }

    public String[] getCommands() {
        return commands;
    }
}

