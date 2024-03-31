package ca.yorku.eecs.groupr.tilttiktok;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

// Class to export the experiment results to a text file

public class ExperimentResultExporter {

    private static void writeFile(File file, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        osw.write(content);
        osw.close();
        fos.close();
    }

    public static boolean write(ExperimentResult result) {
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TiltTikTok_ExperimentResults");
        if (!outputDir.exists() && !outputDir.mkdirs()) return false;

        String experimentTimeOutputFilename = new SimpleDateFormat("yyMMddHHmmss", Locale.CANADA).format(result.getExperimentStartTime()) + "_P" + result.getSetup().getSubjectID() + "_" + result.getSetup().getControlMethod().toString() + ".txt";

        // Write the time data
        StringBuilder experimentDataStr = new StringBuilder();
        experimentDataStr.append("Subject ID: ").append(result.getSetup().getSubjectID()).append("\n");
        experimentDataStr.append("Control Method: ").append(result.getSetup().getControlMethod().toString()).append("\n");
        experimentDataStr.append("Trials: ").append(result.getSetup().getTrials()).append("\n");
        experimentDataStr.append("Experiment Start Time: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).format(result.getExperimentStartTime())).append("\n");
        experimentDataStr.append("\n");

        long[] trialDurations = result.getDurationEachTrial();
        for (int i = 0; i < trialDurations.length; ++i) {
            experimentDataStr.append(Math.round(trialDurations[i] / 1000.0));
            experimentDataStr.append(i == trialDurations.length - 1 ? "\n" : ",");
        }

        int[] incorrectTimes = result.getIncorrectActionEachTrial();
        for (int i = 0; i < incorrectTimes.length; ++i) {
            experimentDataStr.append(incorrectTimes[i]);
            experimentDataStr.append(i == incorrectTimes.length - 1 ? "\n" : ",");
        }
        ;
        try {
            writeFile(new File(outputDir, experimentTimeOutputFilename), experimentDataStr.toString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
