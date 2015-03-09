import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

/**
 * Created by jamieh911 on 02/03/15.
 */
public class ProjectTest {

    public static void main(String[] args) {
        new ProjectTest();
    }

    public ProjectTest() {
        try {
            JVision vs = new JVision();
            BufferedImage originalImage = ImageOp.readInImage("project_files/glaucoma1_crop.jpg");
            BufferedImage preprocessedImage;
            BufferedImage thresholdedImage;
            BufferedImage postprocessedImage;
            displayAnImage(originalImage, vs, 0,0, "Image 1");
            createAndDisplayHistogram(originalImage, vs, 301, 0, "Image 1 Hist");
            preprocessedImage = preprocessImage(originalImage, vs);
            thresholdedImage = thresholdAnImage(preprocessedImage);
            postprocessedImage = postProcessAnImage(thresholdedImage);
            displayAnImage(postprocessedImage, vs, 0, 301, "Threshold Image");
            createAndDisplayHistogram(preprocessedImage, vs, 301, 301, "Threshold Image Histogram");

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private BufferedImage preprocessImage(BufferedImage originalImage, JVision vs) throws HistogramException {
        Histogram hist = new Histogram(originalImage);
        BufferedImage enhancedImage = ImageOp.pixelop(originalImage, histogramEqualisationLut(hist));
        GraphPlot gp = new GraphPlot(histogramEqualisationLut(hist));
        displayAnImage(gp, vs, 602, 0, "Transfer Function");
        return enhancedImage;
    }

    public void displayAnImage(BufferedImage img, JVision display, int x, int y, String title)

    {
        display.imdisp(img,title,x,y);

    }

    public BufferedImage thresholdAnImage(BufferedImage image) {
        int mean = mean(image);
        int sd = calculateSD(mean, image);
        int t1 = (int) (mean + 1.6*sd);
        int t2 = (int) (mean - 0.3*sd);
        return ImageOp.pixelop(image, thresholdLut(t1, t2));
    }

    private short[] thresholdLut(int t1, int t2) {
        short[] lut = new short[256];
        for(int i =0; i < 256; i++) {
            if(i >= t1 || i <= t2){
                lut[i] = 255;
            }
            else {
                lut[i] = 0;
            }
        }
        return lut;
    }

    private int calculateSD(int mean, BufferedImage img) {
        Raster raster = img.getRaster();
        int height = img.getHeight();
        int width = img.getWidth();

        int total = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                total += Math.pow((raster.getSample(j,i,0) - mean),2);
            }
        }
        total = (int) Math.sqrt(total/(height*width)-1);
        return total;
    }

    public BufferedImage postProcessAnImage(BufferedImage image) {
        image = ImageOp.close(image, 8);
        image = ImageOp.open(image, 4);
        return image;
    }

    public BufferedImage performEdgeExtraction(BufferedImage source) {
        final float[] HIGHPASS1X2 = {-1.f,1.f,0.f,0.f,1.f};
        final float[] HIGHPASS2X1 = {-1.f,0.f,1.f,0.f,0.f};
        BufferedImage source1 = ImageOp.convolver(source, HIGHPASS1X2);
        BufferedImage source2 = ImageOp.convolver(source, HIGHPASS2X1);
        return ImageOp.imagrad(source2, source1);
    }

    public short[] powerLawLut(float gamma) {
        short[] lut = new short[256];
        for (int i = 0; i < 256; i++) {
            lut[i] = (short) (Math.pow(i, gamma) / Math.pow(255, gamma - 1));
        }
        return lut;
    }

    public short[] linearStretchLut(float m,float c)

    {
        short[] lut = new short[256];
        for (int i = 0; i < 256; i++) {
            if(i < (-c)/m) {
                lut[i] = 0;
            }
            else if (i > (255 - c)/m) {
                lut[i] = 255;
            }
            else {
                lut[i] = (short) ((m*i) + c);
            }
        }
        return lut;
    }

    public short[] thresholdLut(int t)

    {
        short[] lut = new short[256];
        for(int i =0; i < 256; i++) {
            if(i >= t){
                lut[i] = 255;
            }
            else {
                lut[i] = 0;
            }
        }
        return lut;
    }

    public short[] histogramEqualisationLut (Histogram hist) throws HistogramException {
        short[] lut = new short[256];
        for (int i = 0; i < 256; i++) {
            lut[i] = (short) Math.max(0, ((256 * hist.getCumulativeFrequency(i)/hist.getNumSamples()) - 1));
        }
        return lut;
    }

    public void createAndDisplayHistogram(BufferedImage img,JVision display,int x,int y,String title) throws Exception

    {

        Histogram hist = new Histogram(img);

        GraphPlot gp = new GraphPlot(hist);

        display.imdisp(gp,title,x,y);

    }

    public int mean(BufferedImage src1) {
        Raster src1Raster = src1.getRaster();
        int height = src1Raster.getHeight();
        int width = src1Raster.getWidth();
        int total =0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                total += src1Raster.getSample(j,i,0);
            }
        }
        return total/(height*width);
    }
}
