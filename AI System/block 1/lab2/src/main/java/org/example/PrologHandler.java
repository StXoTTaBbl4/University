package org.example;
import org.jpl7.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PrologHandler {

    private final boolean isConnected;
    private final StringBuilder log = new StringBuilder();

    public PrologHandler() {
        Query facts = new Query(
                "consult",
                new Term[]{new Atom("src/main/resources/facts.pl")}
        );
        Query rules = new Query(
                "consult",
                new Term[]{new Atom("src/main/resources/rules.pl")}
        );
        isConnected = facts.hasSolution() & rules.hasSolution();
    }

    public void handle(String level, String role, String side, boolean meta){

        Variable X = new Variable("X");
        Query q = new Query("персонаж", new Term[]{X});
        ArrayList<String> result = castToArrayList(q.allSolutions());
        logStep("Начало", "", result);

        q = switch (level) {
            case "новый" -> new Query("список_для_новичка", new Term[]{X});
            case "обычный" -> new Query("список_для_среднего", new Term[]{X});
            case "умелый" -> new Query("список_для_опытного", new Term[]{X});
            default -> throw new IllegalStateException("Unexpected value: " + level);
        };

        mergeArrays(result, castToArrayList(q.allSolutions()));
        logStep("Уровень игры", level, result);

        if (!role.equals("все роли")) {
            q = switch (role) {
                case "танки" -> new Query("танки", new Term[]{X});
                case "дамагеры" -> new Query("урон", new Term[]{X});
                case "саппорты" -> new Query("поддержка", new Term[]{X});
                default -> throw new IllegalStateException("Unexpected value: " + level);
            };

            mergeArrays(result, castToArrayList(q.allSolutions()));
            logStep("Роль", role, result);
        }

        if (meta) {
            q = new Query("список_актуальных", new Term[]{X});
            mergeArrays(result, castToArrayList(q.allSolutions()));
            logStep("Метовый", "", result);
        }

        if (side != null) {
            q = switch (side) {
                case "герои" -> new Query("герои", new Term[]{X});
                case "злодеи" -> new Query("злодеи", new Term[]{X});
                case "нейтралы" -> new Query("нейтралы", new Term[]{X});
                default -> throw new IllegalStateException("Unexpected value: " + level);
            };

            mergeArrays(result, castToArrayList(q.allSolutions()));
            logStep("Роль", role, result);
        }

        System.out.println(log);

    }

    private ArrayList<String> castToArrayList(Map<String, Term>[] solutions){
        ArrayList<String> res = new ArrayList<>();
        if (solutions.length == 1) {
            for (Map<String, Term> m : solutions) {
                Compound list = (Compound) m.get("X");
                Term[] elements = list.listToTermArray();
                for (Term t : elements) {
                    res.add(t.toString());
                }
            }
        } else {
            for(Map<String, Term> m: solutions) {
                res.add(m.get("X").toString());
            }
        }

        System.out.println(res);

        return res;
    }

    private void mergeArrays(ArrayList<String> result, ArrayList<String> solutions){
        result.removeIf(obj -> !solutions.contains(obj));
    }

    private void logStep(String name,String level, ArrayList<String> data){
        log.append("Этап: ").append(name).append("\n");
        log.append("Уровень: ").append(level).append("\n");
        log.append("Результаты: ").append("\n").append(data).append("\n");
    }


    public boolean isConnected(){
        return isConnected;
    }

}
