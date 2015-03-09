import java.awt.image.BufferedImage;

public class VisionSystem
{
	public static void main(String[] args)
	{
		new VisionSystem();
	}
		
	//constructor
	public VisionSystem()
	{
		try
		{
			//----------------------------------------------------
			//student's variable declarations and calls to methods
			//----------------------------------------------------
            JVision vs = new JVision();
			BufferedImage image1 = readInImage("boat256.jpg");

			displayAnImage(image1,vs,1,1,"dome image");
//			BufferedImage image2 = readInImage("boat256.jpg");
//			displayAnImage(image2,vs,301,1,"boat image");
//
			createAndDisplayHistogram(image1,vs,1,301,"Boat Histogram");
//			createAndDisplayHistogram(image2,vs,301,301,"Boat Histogram");

            BufferedImage enhancedImage = ImageOp.pixelop(image1, histogramEqualisationLut(new Histogram(image1)));
            GraphPlot gp = new GraphPlot(histogramEqualisationLut(new Histogram(image1)));
            vs.imdisp(gp,"transfer function",301,200);
            displayAnImage(enhancedImage,vs,600,0, "enchanced image");
            createAndDisplayHistogram(enhancedImage,vs,600,301,"enhanced histogram");
					
		}
		catch(Exception e)
		{
			System.out.println("Error message");
			e.printStackTrace();
		}
    }

    public short[] histogramEqualisationLut (Histogram hist) throws HistogramException {
        short[] lut = new short[256];
        for (int i = 0; i < 256; i++) {
            lut[i] = (short) Math.max(0, ((256 * hist.getCumulativeFrequency(i)/hist.getNumSamples()) - 1));
        }
        return lut;
    }

    public short[] brightnessLut(int c)

    {
       short[] lut = new short[256];
        for(int i =0; i < 256; i++) {
            if(i < -c){
                lut[i] = 0;
            }
            else if (i > (255 -c)) {
                lut[i] = 255;
            }
            else {
                lut[i] = (short) (i + c);
            }
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

    public BufferedImage enhanceContrast(BufferedImage source)

    {
       return ImageOp.pixelop(source, linearStretchLut(1.66f, -80));
    }

    public short[] powerLawLut(float gamma) {
        short[] lut = new short[256];
        for (int i = 0; i < 256; i++) {
            lut[i] = (short) (Math.pow(i, gamma) / Math.pow(255, gamma - 1));
        }
        return lut;
    }
    
    //-----------------------
    //student's methods
    //-----------------------
    public BufferedImage readInImage(String file) {
		BufferedImage img;

		img = ImageOp.readInImage(file);
		return img;
	}

	public void displayAnImage(BufferedImage img, JVision display, int x, int y, String title)

	{
		display.imdisp(img,title,x,y);

	}

	public void createAndDisplayHistogram(BufferedImage img,JVision display,int x,int y,String title) throws Exception

	{

		Histogram hist = new Histogram(img);

		GraphPlot gp = new GraphPlot(hist);

		display.imdisp(gp,title,x,y);

	}
    
    
    
}