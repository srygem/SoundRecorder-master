package com.danielkim.soundrecorder.fragments;

public class PreProcess {

    float[] originalSignal;// initial extracted PCM,
    float[] afterEndPtDetection;// after endPointDetection
    public int noOfFrames;// calculated total no of frames
    int samplePerFrame;// how many samples in one frame
    int framedArrayLength;// how many samples in framed array
    public float[][] framedSignal;
    float[] hammingWindow;
    EndPointDetection epd;
    int samplingRate;


    public PreProcess(float[] originalSignal, int samplePerFrame, int samplingRate) {
        this.originalSignal = originalSignal;
        this.samplePerFrame = samplePerFrame;
        this.samplingRate = samplingRate;

        normalizePCM();

	/*	try{
			System.out.println("Sleep start 1  before endPointDetechtion");
			Thread.sleep(10000);
		}catch(Exception e){
			System.out.println("Error");
		}
*/
        epd = new EndPointDetection(this.originalSignal, this.samplingRate);
        afterEndPtDetection = epd.doEndPointDetection();


/*		try{
			System.out.println("Sleep start 1  before Framing and windoing");
			Thread.sleep(10000);
		}catch(Exception e){
			System.out.println("Error");
		}
*/
        doFraming();
        doWindowing();
    }

    private void normalizePCM() {
        float max = originalSignal[0];
        for (int i = 1; i < originalSignal.length; i++) {
            if (max < Math.abs(originalSignal[i])) {
                max = Math.abs(originalSignal[i]);
            }
        }
        System.out.println("max PCM =  " + max);
        for (int i = 0; i < originalSignal.length; i++) {
            originalSignal[i] = originalSignal[i] / max;
        }
    }


    private void doFraming() {
        // calculate no of frames, for framing
        System.out.println("EndPtDetection.length = "+afterEndPtDetection.length+" :: Sample = "+samplePerFrame);
        noOfFrames = 2 * afterEndPtDetection.length / samplePerFrame - 1;
        System.out.println("noOfFrames       " + noOfFrames + "  samplePerFrame     " + samplePerFrame + "  EPD length   "
                + afterEndPtDetection.length);

        framedSignal = new float[noOfFrames][samplePerFrame];
        for (int i = 0; i < noOfFrames; i++) {
            int startIndex = (i * samplePerFrame / 2);
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = afterEndPtDetection[startIndex + j];
            }
        }
    }


    private void doWindowing() {
        // prepare hammingWindow
        hammingWindow = new float[samplePerFrame + 1];

        for (int i = 1; i <= samplePerFrame; i++) {

            hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }
        // do windowing
        for (int i = 0; i < noOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
            }
        }
    }


}
