package net.haesleinhuepf.clijx.plugins;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_localMinimumTouchingNeighborCountMap")
public class LocalMinimumTouchingNeighborCountMap extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized {

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        return localMinimumTouchingNeighborCountMap(getCLIJ2(), (ClearCLBuffer) args[0], (ClearCLBuffer) args[1]);
    }

    public static boolean localMinimumTouchingNeighborCountMap(CLIJ2 clij2, ClearCLBuffer pushed, ClearCLBuffer result) {
        int number_of_labels = (int)clij2.maximumOfAllPixels(pushed);
        ClearCLBuffer touch_matrix = clij2.create(number_of_labels + 1, number_of_labels + 1);
        clij2.generateTouchMatrix(pushed, touch_matrix);

        ClearCLBuffer touch_count_vector = clij2.create(number_of_labels + 1, 1, 1);
        clij2.countTouchingNeighbors(touch_matrix, touch_count_vector);

        ClearCLBuffer minimum_vector = clij2.create(number_of_labels, 1, 1);
        clij2.minimumOfTouchingNeighbors(touch_count_vector, touch_matrix, minimum_vector);
        touch_count_vector.close();
        touch_matrix.close();

        clij2.replaceIntensities(pushed, minimum_vector, result);
        minimum_vector.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "Takes a label map, determines which labels touch, determines for every label with the number of touching \n" +
                "neighboring labels and replaces the label index with the local minimum of this count.\n\n";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }


    @Override
    public String getCategories() {
        return "Visualisation, Graph, Label, Measurements";
    }
}
