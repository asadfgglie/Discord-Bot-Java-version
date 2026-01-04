package ckcsc.asadfgglie.setup;

import ckcsc.asadfgglie.main.Basic;
import ckcsc.asadfgglie.util.Path;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SetUp {
    public static void main(String[] argv) throws IOException {
        if(argv.length == 0){
            setBasicConfigPath("");
            setBasicScriptPath("");
        }
        else if(argv.length == 1){
            if(argv[0].split("=")[0].equals(Option.configpath.getOption())) {
                setBasicConfigPath(argv[0].split("=")[1]);
                setBasicScriptPath("");
            }

            if(argv[0].split("=")[0].equals(Option.scriptpath.getOption())) {
                setBasicScriptPath(argv[0].split("=")[1]);
                setBasicConfigPath("");
            }
        }
        else if(argv.length == 2){
            if(argv[0].split("=")[0].equals(Option.configpath.getOption())) {
                setBasicConfigPath(argv[0].split("=")[1]);
            }
            else if(argv[1].split("=")[0].equals(Option.configpath.getOption())){
                setBasicConfigPath(argv[1].split("=")[1]);
            }

            if(argv[0].split("=")[0].equals(Option.scriptpath.getOption())) {
                setBasicScriptPath(argv[0].split("=")[1]);
            }
            else if(argv[1].split("=")[0].equals(Option.scriptpath.getOption())){
                setBasicScriptPath(argv[1].split("=")[1]);
            }
        }
        else{
            System.out.println(Option.configpath.getInfo());
        }
        Basic.main();
    }

    private static void setBasicConfigPath (@NotNull String path) {
        if(path.startsWith(".")) {
            Basic.CONFIG_PATH = Path.transferPath(Path.getPath() + path.substring(1));
        }
        else if(path.isEmpty()){
            Basic.CONFIG_PATH = Path.transferPath(Path.getPath());
        }
        else {
            Basic.CONFIG_PATH = Path.transferPath(path);
        }
    }

    private static void setBasicScriptPath (@NotNull String path) {
        if(path.startsWith(".")) {
            Basic.SCRIPT_PATH = Path.transferPath(Path.getPath() + path.substring(1));
        }
        else if(path.isEmpty()){
            Basic.SCRIPT_PATH = Path.transferPath(Path.getPath());
        }
        else {
            Basic.SCRIPT_PATH = Path.transferPath(path);
        }
    }
}

enum Option{
    configpath("--configpath",

    "Usage:\n" +
        "java -jar <BotJar>.jar --configpath=<config-folder path>\n\n" +

        "Your config-folder must contain the json file to set up the bot.\n\n" +

        "If you don't set --configpath, Bot will use the Bot's current directory on DEFAULT.\n\n" +

        "Only can use two path representation:\n" +
        "    Absolute path\n" +
        "    ./<dictionary>\n\n" +

        "./ means the current directory."),
    scriptpath("--scriptpath",
        "Usage:\n" +
            "java -jar <BotJar>.jar --scriptpath=<script-folder path>\n\n" +

            "Your config-folder must contain the json file to set up the bot.\n\n" +

            "If you don't set --scriptpath, Bot will use the Bot's current directory on DEFAULT.\n\n" +

            "Only can use two path representation:\n" +
            "    Absolute path\n" +
            "    ./<dictionary>\n\n" +

            "./ means the current directory."
    );

    private final String option, info;

    Option(String option, String info){
        this.option = option;
        this.info = info;
    }

    public String getOption() {
        return option;
    }

    public String getInfo() {
        return info;
    }
}