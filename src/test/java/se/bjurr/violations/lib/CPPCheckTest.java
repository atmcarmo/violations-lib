package se.bjurr.violations.lib;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.lib.TestUtils.getRootFolder;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.CPPCHECK;

import java.util.List;
import org.junit.Test;
import se.bjurr.violations.lib.model.Violation;

public class CPPCheckTest {

  private static final String MSG_1 =
      "The scope of the variable 'n' can be reduced. Warning: It can be unsafe to fix this message. Be careful. Especially when there are inner loops. Here is an example where cppcheck will write that the scope for 'i' can be reduced:&#xa;void f(int x)&#xa;{&#xa;    int i = 0;&#xa;    if (x) {&#xa;        // it's safe to move 'int i = 0' here&#xa;        for (int n = 0; n < 10; ++n) {&#xa;            // it is possible but not safe to move 'int i = 0' here&#xa;            do_something(&i);&#xa;        }&#xa;    }&#xa;}&#xa;When you see this message it is always safe to reduce the variable scope 1 level.";
  private static final String MSG_2 =
      "The scope of the variable 'i' can be reduced. Warning: It can be unsafe to fix this message. Be careful. Especially when there are inner loops. Here is an example where cppcheck will write that the scope for 'i' can be reduced:&#xa;void f(int x)&#xa;{&#xa;    int i = 0;&#xa;    if (x) {&#xa;        // it's safe to move 'int i = 0' here&#xa;        for (int n = 0; n < 10; ++n) {&#xa;            // it is possible but not safe to move 'int i = 0' here&#xa;            do_something(&i);&#xa;        }&#xa;    }&#xa;}&#xa;When you see this message it is always safe to reduce the variable scope 1 level.";

  @Test
  public void testThatViolationsCanBeParsed() {
    String rootFolder = getRootFolder();

    List<Violation> actual =
        violationsApi() //
            .withPattern(".*/cppcheck/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(CPPCHECK) //
            .violations();

    assertThat(actual) //
        .contains( //
            violationBuilder() //
                .setParser(CPPCHECK) //
                .setFile("api.c") //
                .setStartLine(498) //
                .setEndLine(498) //
                .setRule("variableScope") //
                .setMessage(MSG_1) //
                .setSeverity(INFO) //
                .build()) //
        .contains( //
            violationBuilder() //
                .setParser(CPPCHECK) //
                .setFile("api_storage.c") //
                .setStartLine(104) //
                .setEndLine(104) //
                .setRule("variableScope") //
                .setMessage(MSG_2) //
                .setSeverity(ERROR) //
                .build()) //
        .hasSize(3);
  }

  @Test
  public void testThatViolationsCanBeParsedExample1() {
    String rootFolder = getRootFolder();

    List<Violation> actual =
        violationsApi() //
            .withPattern(".*/cppcheck/example1\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(CPPCHECK) //
            .violations();

    Violation violation0 = actual.get(0);
    assertThat(violation0.getMessage()) //
        .isEqualTo("Variable 'it' is reassigned a value before the old one has been used.");

    Violation violation1 = actual.get(1);
    assertThat(violation1.getMessage()) //
        .isEqualTo("Variable 'it' is reassigned a value before the old one has been used.");

    Violation violation2 = actual.get(2);
    assertThat(violation2.getMessage()) //
        .isEqualTo("Condition 'rc' is always true");

    Violation violation3 = actual.get(3);
    assertThat(violation3.getMessage()) //
        .isEqualTo("Condition 'rc' is always true. Assignment 'rc=true', assigned value is 1");
  }
}
