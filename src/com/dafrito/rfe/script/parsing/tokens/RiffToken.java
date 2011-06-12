/**
 * 
 */
package com.dafrito.rfe.script.parsing.tokens;

import com.bluespot.parsing.Token;
import com.dafrito.rfe.script.parsing.ScriptElement;

/**
 * A RiffScript {@link Token}. This is used to ease the transition from
 * {@link ScriptElement} since this class's visitor type can be found at
 * runtime.
 * 
 * @author Aaron Faanes
 * 
 */
public interface RiffToken extends Token<RiffTokenVisitor> {

}
