package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.parser;

import org.apache.commons.math3.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScenarioParser {
    private static JSONParser jsonParser = new JSONParser();

    private static OperationType getOperationFromString(String op) throws ParsingException {
        switch (op){
            case "CREATE" :
                return OperationType.CREATE;
            case "DELETE" :
                return OperationType.DELETE;
            case "SEND" :
                return OperationType.SEND;
            default:
                throw new ParsingException();
        }
    }
    private static CibleType     getCibleFromString(String cible) throws ParsingException {
        switch (cible) {
            case "subscriber" :
                return CibleType.SUBSCRIBER;
            case "publisher" :
                return CibleType.PUBLISHER;
            default:
                throw new ParsingException();
        }
    }

    public static Scenario readScenario(String scenario)
        throws IOException, ParsingException, ParseException {
        // read scenario file
        String filePath = "scenarios/" + scenario + ".scenario";
        Scanner reader = new Scanner(new File(filePath));
        // read expected file (json)
        String filePathExpected = "scenarios/" + scenario + ".expected";
        JSONObject expected;
        try {
            expected = (JSONObject) jsonParser.parse(new FileReader(filePathExpected));
        } catch (ClassCastException e) {
            throw new ParsingException();
        }

        List<StepScenario> steps = new ArrayList<>();
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            if (line.charAt(0) == '#') {
                continue;
            }
            String[] data = line.split(",");
            OperationType type = getOperationFromString(data[0]);
            CibleType ct = getCibleFromString(data[1]);
            String nameSubScenario = data[2];
            String uriCible = data[3];
            ScenarioComponent subScenario;
            int valueExpected;
            if (ct.equals(CibleType.PUBLISHER)) {
                valueExpected = -1;
                subScenario = readPublisherScenario(
                    "scenarios/publishers/" + nameSubScenario + ".json"
                );
            } else if (ct.equals(CibleType.SUBSCRIBER)) {
                try {
                    valueExpected = Math.toIntExact((Long) expected.get(uriCible));
                } catch (ClassCastException e) {
                    throw new ParsingException();
                }
                subScenario = readSubscriberScenario(
                    "scenarios/subscribers/" + nameSubScenario + ".json"
                );
            } else {
                throw new ParsingException();
            }
            Cible c = new Cible(ct, uriCible, subScenario, valueExpected);
            steps.add(new StepScenario(type, c));
        }
        return new Scenario(steps);
    }

    private static ScenarioComponent readPublisherScenario(String filename)
        throws IOException, ParseException, ParsingException {
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(filename));
        int repeat;
        try{
            repeat = Math.toIntExact((Long) json.get("repeated"));
        } catch (ClassCastException cce) {
            throw new ParsingException();
        }
        List<StepComponent> steps = new ArrayList<>();
        JSONArray jsonSteps = (JSONArray) json.get("messages");
        for (int i = 0; i < jsonSteps.size(); i++) {
            try {
                JSONObject temp = (JSONObject) jsonSteps.get(i);
                String content = (String) temp.get("content");
                int timeToWait = Math.toIntExact((Long) temp.get("temps"));
                List<String> topics = new ArrayList<>();
                JSONArray topicsJson = (JSONArray) temp.get("topics");
                for (Object obj : topicsJson) {
                    topics.add((String) obj);
                }
                List<Pair<Object, Object>> filters = new ArrayList<>();
                JSONArray filtersJson = (JSONArray) temp.get("filters");
                for (Object obj : filtersJson) {
                    JSONObject jsObj = (JSONObject) obj;
                    filters.add(
                        new Pair<>(
                            jsObj.get("name"),
                            jsObj.get("value")
                        )
                    );
                }
                StepComponent stepComponent = new StepComponentPublisher(
                    OperationType.SEND,
                    content,
                    filters,
                    timeToWait,
                    topics
                );
                steps.add(stepComponent);
            } catch (ClassCastException cce) {
                throw new ParsingException();
            }
        }
        List<StepComponent> finalSteps = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            finalSteps.addAll(steps);
        }
        return new ScenarioComponentPublisher(finalSteps);
    }

    private static ScenarioComponent readSubscriberScenario(String filename)
        throws IOException, ParseException, ParsingException {
        JSONObject json = (JSONObject) jsonParser.parse(new FileReader(filename));
        JSONArray subscriptions = (JSONArray) json.get("subscriptions");
        List<StepComponent> steps = new ArrayList<>();
        for(Object obj : subscriptions) {
            try {
                JSONObject jsObj = (JSONObject) obj;
                String topic = (String) jsObj.get("topic");
                List<Pair<Object, Object>> filters = new ArrayList<>();
                JSONArray filtersJson = (JSONArray) jsObj.get("filters");
                for (Object filterObj : filtersJson) {
                    JSONObject filterJS = (JSONObject) filterObj;
                    filters.add(
                        new Pair<>(
                            filterJS.get("name"),
                            filterJS.get("value")
                        )
                    );
                }
                steps.add(new StepComponentSubscriber(
                    OperationType.SEND,
                    topic,
                    filters
                ));
            } catch (ClassCastException cce) {
                throw new ParsingException();
            }
        }
        return new ScenarioComponentSubscriber(steps);
    }


}
