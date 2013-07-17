/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;

public interface InputConverter {

    Object convertInput(String commandParameter,
                        InputConversionEngine inputConversionEngine) throws InputConversionException;
}
