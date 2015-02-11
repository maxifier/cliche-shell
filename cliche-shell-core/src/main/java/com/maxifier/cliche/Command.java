/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;

import jline.console.completer.Completer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Annotation for commands. Allows to specify the name of a command, otherwise method's name is used.
 *
 * @author ASG
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Allows to override default command name, which is derived from method's name
     *
     * @return "" or null if default name is used, user-specified name otherwise.
     */
    String name() default ""; // if "" then Null is assumed.

    /**
     * Specify the description of the command. Default description (if this
     * property is not set) says "methodName(Arg1Type, Arg2Type,...) : ReturnType".
     *
     * @return command's description or "" if not set.
     */
    String description() default "";

    /**
     * Specify the shortcut name for the command.
     * If not set, if the name attribute is not set as well, the Shell takes
     * the first letter of each word (void selectUser() --- select-user --- su).
     *
     * @return command's abbreviation or "" if not set.
     */
    String abbrev() default "";

    /**
     * Specify the string to output before command's output, i.e. some explanations.
     *
     * @return command's header or "" if not set.
     */
    String header() default "";


    /**
     * Allows command-UI to call this command automatically and very often.<p/>
     * So command-UI treats the result of this command as a actual screen like linux command 'top',
     * which refreshes screens of the current processes very often.<p/>
     * This command should not start any process, it should return just representation of process or server-side activity.
     *
     * @return true if the command is followable, so it can be automatically called(refreshed) by command-UI.
     */
    boolean followable() default false;


    /**
     * Specify the Class which will implement auto-complete logic. The class should return list of possible options by terms.
     *
     * @return command's header or "" if not set.
     */
    Class<? extends Completer> completer() default DEFAULT_COMPLETER.class;

    static final class DEFAULT_COMPLETER implements Completer {
        @Override
        public int complete(String buffer, int cursor, List<CharSequence> candidates) {
            return 0;
        }
    }

}
