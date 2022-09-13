package org.acme;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// export PATH=$PATH:/usr/lib/jvm/graalvm/graalvm-ce-java17-22.2.0/bin
//./mvnw package -Pnative
//./target/getting-started-1.0.0-SNAPSHOT-runner

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/gpt3")
    public String askGgpt3(@QueryParam("prompt") String prompt) throws IOException, InterruptedException {
        return getCompletion(prompt);
    }


    //a method that calls the api of gpt3
    private String getCompletion(String prompt) {
        String json = """
                {
                            "model": "text-davinci-002",
                            "prompt": "%prompt%",
                            "temperature": 0.7,
                            "max_tokens": 2048,
                            "top_p": 1,
                            "frequency_penalty": 0,
                            "presence_penalty": 0
                    }
                """;
        json = json.replace("%prompt%", prompt);
        String url = "https://api.openai.com/v1/completions";

        //make request to gpt3
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        //extract the text element from the response
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            JSONObject resp = new JSONObject(body);
            JSONArray choices = resp.getJSONArray("choices");
            JSONObject choice = (JSONObject) choices.get(0);
            String text = choice.getString("text");
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}