package org.jruby.runtime.callsite;

import org.jruby.RubyFixnum;
import org.jruby.RubyFloat;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class MinusCallSite extends BimorphicCallSite {

    public MinusCallSite() {
        super("-");
    }

    @Override
    public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, long arg1) {
        if (self instanceof RubyFixnum) {
            if (cache instanceof FixnumEntry) return ((RubyFixnum) self).op_minus(context, arg1);
        } else if (self instanceof RubyFloat) {
            if (secondaryCache instanceof FloatEntry) return ((RubyFloat) self).op_minus(context, arg1);
        }
        return super.call(context, caller, self, arg1);
    }

    @Override
    public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject arg1) {
        if (self instanceof RubyFixnum) {
            if (cache instanceof FixnumEntry) return ((RubyFixnum) self).op_minus(context, arg1);
        } else if (self instanceof RubyFloat) {
            if (secondaryCache instanceof FloatEntry) return ((RubyFloat) self).op_minus(context, arg1);
        }
        return super.call(context, caller, self, arg1);
    }

    @Override
    public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, double arg1) {
        if (self instanceof RubyFixnum) {
            if (cache instanceof FixnumEntry) return ((RubyFixnum) self).op_minus(context, arg1);
        } else if (self instanceof RubyFloat) {
            if (secondaryCache instanceof FloatEntry) return ((RubyFloat) self).op_minus(context, arg1);
        }
        return super.call(context, caller, self, arg1);
    }

    @Override
    protected CacheEntry setCache(final CacheEntry entry, final IRubyObject self) {
        // used as a primary cache - for Fixnum targets
        if (self instanceof RubyFixnum && entry.method.isBuiltin()) {
            return cache = new FixnumEntry(entry); // tagged entry replacement - a (costly) isBuiltin replacement
        }
        return cache = entry;
    }

    @Override
    protected CacheEntry setSecondaryCache(final CacheEntry entry, final IRubyObject self) {
        // used as a primary cache - for Float targets
        if (self instanceof RubyFloat && entry.method.isBuiltin()) {
            return secondaryCache = new FloatEntry(entry); // tagged entry replacement - a (costly) isBuiltin replacement
        }
        return secondaryCache = entry;
    }

    @Override
    public boolean isBuiltin(final IRubyObject self) {
        if (self instanceof RubyFixnum && cache instanceof FixnumEntry) return true;
        return super.isBuiltin(self);
    }

    @Override
    public boolean isSecondaryBuiltin(final IRubyObject self) {
        if (self instanceof RubyFloat && secondaryCache instanceof FloatEntry) return true;
        return super.isSecondaryBuiltin(self);
    }

    private static class FixnumEntry extends CacheEntry {

        FixnumEntry(CacheEntry entry) {
            super(entry.method, entry.sourceModule, entry.token);
        }

    }

    private static class FloatEntry extends CacheEntry {

        FloatEntry(CacheEntry entry) {
            super(entry.method, entry.sourceModule, entry.token);
        }

    }

}
