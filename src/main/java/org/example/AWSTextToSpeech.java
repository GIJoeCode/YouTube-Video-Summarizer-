package org.example;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;
import software.amazon.awssdk.core.ResponseInputStream;
import java.io.*;

public class AWSTextToSpeech {

    public static void convertTextToSpeech(String text, String outputFilePath) {
        PollyClient polly = PollyClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try {
            SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
                    .text(text)
                    .voiceId(VoiceId.JOANNA)
                    .outputFormat(OutputFormat.MP3)
                    .build();

            ResponseInputStream<SynthesizeSpeechResponse> synthRes = polly.synthesizeSpeech(synthReq);
            InputStream audioStream = synthRes;

            saveAudioToFile(audioStream, outputFilePath);
            System.out.println("Text-to-speech conversion completed. Output saved to: " + outputFilePath);
        } catch (PollyException e) {
            System.err.println("Failed to convert text to speech: " + e.getMessage());
        } finally {
            polly.close();
        }
    }

    private static void saveAudioToFile(InputStream audioStream, String outputFilePath) {
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            while ((readBytes = audioStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            System.err.println("Error saving audio to file: " + e.getMessage());
        }
    }
}
