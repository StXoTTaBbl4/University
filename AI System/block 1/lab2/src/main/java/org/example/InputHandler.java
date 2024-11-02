package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHandler {
    private final String BASIC_REGEXP = "Я (новый|обычный|умелый) игрок\\. Меня интересуют (все роли|танки|саппорты|дамагеры)\\. (Каких персонажей мне стоит попробовать|Какие метовые персонажи мне подойдут)\\?";
    private final String EXTENDED_REGEXP = "Я (новый|обычный|умелый) игрок\\. Меня интересуют (все роли|танки|саппорты|дамагеры)\\. (Каких персонажей мне стоит попробовать|Какие метовые персонажи мне подойдут), если мне нравятся (герои|злодеи|нейтралы)\\?";
    private final Pattern basic_pattern = Pattern.compile(BASIC_REGEXP);
    private final Pattern extended_pattern = Pattern.compile(EXTENDED_REGEXP);

    private final PrologHandler prologHandler = new PrologHandler();

    private String level;
    private String role;
    private String side;
    private boolean meta = false;

    public InputHandler() {
        if (!prologHandler.isConnected()){
            System.out.println("Не подключены с правилами и/или фактами.");
            System.exit(1);
        }
    }

    public void handle(String input){
        if (!pareInput(input)){
            showPatternWarning();
            return;
        }

        prologHandler.handle(level, role, side, meta);
    }

    private boolean pareInput(String input){
        Matcher matcher = basic_pattern.matcher(input);
        if (matcher.matches()){
            level = matcher.group(1);
            role = matcher.group(2);
            meta = matcher.group(3).equals("Какие метовые персонажи мне подойдут");
            side = null;
            return true;
        }

        matcher = extended_pattern.matcher(input);
        if (matcher.matches()){
            level = matcher.group(1);
            role = matcher.group(2);
            meta = matcher.group(3).equals("Какие метовые персонажи мне подойдут");
            side = matcher.group(4);
            return true;
        }

        return false;
    }

    private void showPatternWarning(){
        System.out.println("Запрос должен соответствовать одному из паттернов:\n"+
                BASIC_REGEXP +"\n"+
                EXTENDED_REGEXP);
    }

    @Override
    public String toString() {
        return "level='" + level + '\'' +
                ", role='" + role + '\'' +
                ", side='" + side + '\'' +
                ", meta=" + meta;
    }
}
