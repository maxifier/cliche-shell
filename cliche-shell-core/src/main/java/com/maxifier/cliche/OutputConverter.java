/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;

import java.lang.reflect.Method;

public interface OutputConverter {

    Object convertOutput(Object toBeFormatted, Method commandMethod);
}
