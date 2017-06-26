package com.comptel.bst.tools.mergetool;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    BSTMergeToolTest.class,
    BSTParserTest.class,
    MergeConflictTest.class,
    MergeCompleteTest.class,
    MergeApplyTest.class
})
public class AllTestsSuite {

}
