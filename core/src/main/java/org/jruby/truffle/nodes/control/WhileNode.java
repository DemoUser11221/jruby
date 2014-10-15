/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.control;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.source.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import com.oracle.truffle.api.utilities.*;
import org.jruby.truffle.nodes.*;
import org.jruby.truffle.nodes.cast.*;
import org.jruby.truffle.runtime.*;
import org.jruby.truffle.runtime.control.*;

/**
 * Represents a Ruby {@code while} statement.
 */
public class WhileNode extends RubyNode {



    @Child protected BooleanCastNode condition;
    @Child protected RubyNode body;

    private final BranchProfile breakProfile = BranchProfile.create();
    private final BranchProfile nextProfile = BranchProfile.create();
    private final BranchProfile redoProfile = BranchProfile.create();

    public WhileNode(RubyContext context, SourceSection sourceSection, BooleanCastNode condition, RubyNode body) {
        super(context, sourceSection);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        int count = 0;

        try {
            outer: while (condition.executeBoolean(frame)) {
                while (true) {
                    if (CompilerDirectives.inInterpreter()) {
                        count++;
                    }

                    try {
                        body.execute(frame);
                        continue outer;
                    } catch (BreakException e) {
                        breakProfile.enter();
                        return e.getResult();
                    } catch (NextException e) {
                        nextProfile.enter();
                        continue outer;
                    } catch (RedoException e) {
                        redoProfile.enter();
                    }
                }
            }
        } finally {
            if (CompilerDirectives.inInterpreter()) {
                ((RubyRootNode) getRootNode()).reportLoopCountThroughBlocks(count);
            }
        }

        return NilPlaceholder.INSTANCE;
    }

}
