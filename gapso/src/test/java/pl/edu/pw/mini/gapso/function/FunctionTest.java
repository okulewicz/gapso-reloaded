package pl.edu.pw.mini.gapso.function;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class FunctionTest {

    @Test
    public void isTargetReached() {
        FunctionWhiteBox functionFullSquare = new ConvexSquareFunction();
        FunctionWhiteBox functionSeparableSquare = new ConvexSeparableSquareFunction();
        final double[] someX = {2.0, 2.0};
        double[] opt1 = functionFullSquare.getOptimumLocation();
        double[] opt2 = functionSeparableSquare.getOptimumLocation();

        testForOptAndFunction(functionFullSquare, someX, opt1);
        testForOptAndFunction(functionSeparableSquare, someX, opt2);

        FunctionWhiteBox functionPartiallyFalt = new PartiallyFlatLinearFunction();
        testFunctionBeforeAndAfterSomeEvaluation(functionPartiallyFalt, someX);
    }

    private void testForOptAndFunction(FunctionWhiteBox function, double[] someX, double[] opt) {
        Assert.assertFalse(Arrays.equals(opt, someX));
        testFunctionBeforeAndAfterSomeEvaluation(function, someX);
        function.getValue(opt);
        Assert.assertTrue(function.isTargetReached());
        function.resetOptimumVisitedState();
        Assert.assertFalse(function.isTargetReached());
    }

    private void testFunctionBeforeAndAfterSomeEvaluation(FunctionWhiteBox function, double[] someX) {
        Assert.assertFalse(function.isTargetReached());
        function.getValue(someX);
        Assert.assertFalse(function.isTargetReached());
    }
}