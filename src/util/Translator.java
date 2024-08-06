package util;

import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * This class implements the only public method - translating text according to
 * words in sets of 10, translated in parallel. Unfortunately, most of the
 * high-quality translation APIs provided by large companies, such as Yandex/Google,
 * are paid, and free access is limited by the number of requests. Therefore,
 * I found a completely free, but not the highest quality API - Memory Translate.
 * I think this is the best option for this laboratory work, because it is important
 * to demonstrate the ability to work with external APIs and you can test the program
 * without fear of restrictions.
 */

public final class Translator {

    private static final String MEMORY_TRANSLATE_URL = "https://api.mymemory.translated.net/get?q={text}&langpair={fromLang}|{toLang}";

    private final String fromLang;
    private final String toLang;
    private final RestTemplate restTemplate;


    /**
     * Creates a translator to translate from fromLang to toLang using method {@code public String parallelTranslate(String text)}
     * @param fromLang source language of translation
     * @param toLang target language of translation
     */

    public Translator(String fromLang, String toLang) {
        this.fromLang = fromLang;
        this.toLang = toLang;
        this.restTemplate = new RestTemplate();
    }


    /**
     * Splits the original text by whitespaces, translates every word separately,
     * translations are performed parallel, but no more than in 10 streams at every moment.
     * Results are joined by spaces in matching order and returned as a result of function.
     *
     * @param text an original text to be translated
     * @return result of separated translation of original text
     * @throws InterruptedException if one of process will be interrupted
     * @throws ExecutionException if execution error occurs during the performing translation
     * @throws RestClientException if connection to the API-server occurs
     * @throws JSONException if API-server response json has unexpected format
     */

    public String parallelTranslate(String text) throws InterruptedException, ExecutionException {
        List<String> originalWords = Arrays.asList(text.split("\\s+"));
        List<Callable<String>> tasks = new ArrayList<>();
        StringBuilder translatedText = new StringBuilder();
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < originalWords.size(); i++) {
                String word = originalWords.get(i);
                tasks.add(() -> translate(word));
                if (i % 10 == 9 || i == originalWords.size() - 1) {
                    List<Future<String>> results = executor.invokeAll(tasks);
                    for (Future<String> result : results) {
                        translatedText.append(result.get()).append(" ");
                    }
                    tasks.clear();
                }
            }
            return translatedText.deleteCharAt(translatedText.length() - 1).toString();
        } finally {
            executor.shutdown();
        }
    }

    private String translate(String word) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                MEMORY_TRANSLATE_URL, String.class, word, fromLang, toLang);
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            if (responseBody == null) {
                throw new HttpClientErrorException(HttpStatus.NO_CONTENT, "Response body is null");
            }
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getJSONArray("matches").getJSONObject(1).getString("translation");
        } else {
            throw new HttpClientErrorException(response.getStatusCode(),
                    "Received message:" + System.lineSeparator() + response.getBody());
        }
    }
}
