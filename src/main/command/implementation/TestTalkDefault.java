package main.command.implementation;

import main.command.interfaces.ITalk;

/**
 * An example implementation of the Talk command
 */
public class TestTalkDefault implements ITalk {
    @Override
    public String talk() {
        return "I can talk!!";
    }
}
