package ckcsc.asadfgglie.main.services;

import ckcsc.asadfgglie.main.Basic;
import ckcsc.asadfgglie.main.services.Register.Services;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GFloor extends Services {
    private long nowFloor = 0;
    private long maxFloor = 0;
    private @Nullable User lastFloorBreaker;
    private long lastFloorBuilderID = -1;
    private boolean isNeedPin = false;
    /**
     * < MessageIDLong, floor >
     */
    private final HashMap<Long, Long> floorHistory = new HashMap<>();

    private long CHANNEL_ID;

    public GFloor(){}

    @Override
    public Services copy() {
        return new GFloor();
    }

    @Override
    public void registerByEnvironment(@NotNull JSONObject values) {
        try {
            this.CHANNEL_ID = values.getLong("CHANNEL_ID");
        }
        catch (JSONException e) {
            logger.error("Need \"CHANNEL_ID\" to register service.", e);
        }

        try {
            this.nowFloor = values.getLong("nowFloor");
        }
        catch (JSONException e) {
            logger.warn("\"nowFloor\" is a option to register service. Set to the default value: 0");
        }

        try {
           this.maxFloor = values.getLong("maxFloor");
        }
        catch (JSONException e) {
            logger.warn("\"maxFloor\" is a option to register service. Set to the default value: 0");
        }

        try {
            this.lastFloorBuilderID = values.getLong("lastFloorBuilderID");
        }
        catch (JSONException e) {
            logger.warn("\"lastFloorBuilderID\" is a option to register service. Set to the default value: -1");
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){
        if(event.getChannel().getIdLong() == CHANNEL_ID){
            MessageType messageType = event.getMessage().getType();

            if(messageType == MessageType.DEFAULT || messageType == MessageType.INLINE_REPLY) {
                printMsg(event);

                if (event.getAuthor() == event.getJDA().getSelfUser()) {
                    if(isNeedPin) {
                        event.getChannel().pinMessageById(event.getMessageId()).queue();
                        isNeedPin = false;
                    }
                } else {
                    gCheck(event);

                    gUpdateConfig();

                    Basic.saveConfig(Basic.REGISTER_CONFIG);
                }
            }
        }
    }

    @Override
    public void onMessageUpdate (@NotNull MessageUpdateEvent event) {
        if(floorHistory.containsKey(event.getMessageIdLong())){
            printMsg(event);
            gUpdateCheck(event);
        }
    }

    private void gUpdateCheck (@NotNull MessageUpdateEvent event) {
        String message = event.getMessage().getContentDisplay();
        String[] messageArray = message.split("\n");

        String msg = messageArray[0];
        msg = msg.split("\\s+")[0];

        long floor;
        try
        {
            floor = Long.parseLong(msg.split("g")[1]);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            breakFloor(event, "????????????????????????");
            return;
        }
        if (!msg.matches("g\\d+")) {
            breakFloor(event, "?????????g?????????????");
        }
        if(floor != floorHistory.get(event.getMessageIdLong())){
            breakFloor(event, "????????????????????????");
        }
        printlnInfo(null);
    }

    @Override
    public void onMessageDelete (@NotNull MessageDeleteEvent event) {
        if(floorHistory.containsKey(event.getMessageIdLong())){
            breakFloor(event, "?????????????????????????????????!");
            printlnInfo("Floor was destroyed!");
        }
    }

    private void gCheck(@NotNull MessageReceivedEvent event){
        String message = event.getMessage().getContentDisplay();
        String[] messageArray = message.split("\n");

        String msg = messageArray[0];
        msg = msg.split("\\s+")[0];

        gCheckImplement(msg, event);

        printlnInfo(null);
    }

    private void gUpdateConfig() {
        JSONObject selfService = getSelfConfig();

        selfService.put("maxFloor", this.maxFloor);
        selfService.put("nowFloor", this.nowFloor);
        selfService.put("lastFloorBuilderID", this.lastFloorBuilderID);
    }

    private void gCheckImplement(String msg, @NotNull MessageReceivedEvent event) {
        long floor;

        try
        {
            floor = Long.parseLong(msg.split("g")[1]);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            breakFloor(event, "????????????????????????");
            return;
        }

        if (!msg.matches("g\\d+"))
        {
            breakFloor(event, "????????????????????????");
        }
        else if (floor - nowFloor != 1)
        {
            breakFloor(event, "??????????????????BNT??????????????????????????????:\"??????????????????????????????????????????????\"\n\t\t???????????????????????????????????????");
        }
        else if(lastFloorBuilderID == event.getAuthor().getIdLong()){
            breakFloor(event, "???????????????????????????????????????G?????????");
        }
        else
        {
            floorHistory.put(event.getMessage().getIdLong(), floor);
            lastFloorBuilderID = event.getAuthor().getIdLong();
            nowFloor++;

            if (nowFloor > maxFloor)
            {
                maxFloor = nowFloor;
                isNeedPin = true;
            }
        }
    }

    private void breakFloor(@NotNull GenericMessageEvent e, String reason){
        if(e instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) e;
            lastFloorBreaker = event.getMessage().getAuthor();
        }
        else if(e instanceof MessageDeleteEvent){
            lastFloorBreaker = null;
        }

        MessageChannel channel = e.getChannel();
        channel.sendMessage(gMessage(reason)).queue();

        lastFloorBuilderID = -1;
        nowFloor = 0;
        floorHistory.clear();
    }

    private String gMessage(String reason){
        if (lastFloorBreaker != null) {
            return "----------------------------\n" +
                   "???????????????\t" + nowFloor + " ???\n" +
                   "????????????\t\t" + lastFloorBreaker.getAsMention() + "\n" +
                   "???????????????\t" + maxFloor + " ???\n" +
                   "?????????\t\t" + reason + "\n" +
                   "----------------------------";
        }
        else{
            return "----------------------------\n" +
                    "???????????????\t" + nowFloor + " ???\n" +
                    "???????????????\t" + maxFloor + " ???\n" +
                    "?????????\t\t" + reason + "\n" +
                    "----------------------------";
        }
    }

    @Override
    public String toString(){
        return GFloor.class.getSimpleName() + "(serviceName: " + this.serviceName + ", CHANNEL_ID: " + this.CHANNEL_ID + ", nowFloor: " + this.nowFloor + ", maxFloor: " + this.maxFloor + ", lastFloorBuilder" + this.lastFloorBuilderID + ")";
    }

}
