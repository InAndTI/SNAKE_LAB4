package org.example;
import org.example.logics.*;

import java.util.Random;

public class Message {
    //private SnakesProto.GameConfig gameConfig;
    private static int state_order = 0;
    private final Game game;
    public Message(Game game){
        this.game = game;
    }
    private SnakesProto.GameConfig createField(){
        return SnakesProto.GameConfig.newBuilder().setWidth(game.field.getWidth()/game.field.sizeBlock).
                setHeight(game.field.getHeight()/game.field.sizeBlock).setFoodStatic(game.field.getCountFood()).setStateDelayMs(game.time).build();

    }
    private SnakesProto.GamePlayer createGamer(Snake snake){
        return SnakesProto.GamePlayer.newBuilder().setName(snake.name).setId(snake.id).setIpAddress(snake.ip_address)
                .setPort(snake.port).setRole(snake.nodeRole).setScore(snake.size-2).build();
    }
    private SnakesProto.GamePlayers createGamePlayers(){
        SnakesProto.GamePlayers.Builder players = SnakesProto.GamePlayers.newBuilder();
        int i = 0;
        for (Snake s:
             game.getSnake().values()) {
            players.addPlayers(createGamer(s));
            i++;
        }
        return players.build();
    }

    private SnakesProto.GameState createGameState(){
        SnakesProto.GameState.Builder gameState = SnakesProto.GameState.newBuilder().setStateOrder(state_order);
        state_order++;
        int i = 0;
        for (Snake s:
                game.getSnake().values()) {
            gameState.addSnakes(createSnake(s));
            i++;
        }
        i = 0;
        for (Coord c:
                game.getFood()) {
            gameState.addFoods(SnakesProto.GameState.Coord.newBuilder().setX(c.x/game.field.sizeBlock).setY(c.y/game.field.sizeBlock));
            i++;
        }
        gameState.setPlayers(createGamePlayers());
        return gameState.build();
    }
    private SnakesProto.GameState.Snake createSnake(Snake s){
        SnakesProto.GameState.Snake.Builder snake = SnakesProto.GameState.Snake.newBuilder().setPlayerId(s.id);
        int i = 0;
        for (Snake.Point p:
             s.getSnakePoints()) {
            snake.addPoints(i, SnakesProto.GameState.Coord.newBuilder().setX(p.getXY().x/game.field.sizeBlock).setY(p.getXY().y/game.field.sizeBlock));
            i++;
        }
        snake.setState(s.snakeState);
        snake.setHeadDirection(SnakesProto.Direction.valueOf(s.direction.name()));
        return snake.build();
    }
    private SnakesProto.GameAnnouncement createGameAnnouncement(){
        return SnakesProto.GameAnnouncement.newBuilder().setGameName(game.name).setConfig(createField())
                .setPlayers(createGamePlayers()).setCanJoin(game.can_join).build();
    }
    public SnakesProto.GameMessage PingMsg(){
        //return SnakesProto.GameMessage.newBuilder().
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).setPing(SnakesProto.GameMessage.newBuilder()
                .getPing()).build();
    }
    public SnakesProto.GameMessage SteerMsg(SnakesProto.Direction direction){
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).setSteer(SnakesProto.GameMessage.newBuilder()
                .getSteerBuilder().setDirection(direction)).build();
    }
    public SnakesProto.GameMessage AckMsg(long seq, int sender_id, int receiver_id){
        return SnakesProto.GameMessage.newBuilder().setSenderId(sender_id).setReceiverId(receiver_id).setMsgSeq(seq).
                setAck(SnakesProto.GameMessage.newBuilder().getAck()).build();
    }
    public SnakesProto.GameMessage StateMsg(){
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).setState(SnakesProto.GameMessage.newBuilder()
                .getStateBuilder().setState(createGameState())).build();
    }
    public SnakesProto.GameMessage AnnouncementMsg(){
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).setAnnouncement(SnakesProto.GameMessage.newBuilder()
                .getAnnouncementBuilder().addGames(createGameAnnouncement())).build();
    }
    public SnakesProto.GameMessage DiscoverMsg(){
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).
                setDiscover(SnakesProto.GameMessage.newBuilder().getDiscover()).build();
    }
    public SnakesProto.GameMessage JoinMsg(String playerName, String gameName, SnakesProto.NodeRole nodeRole){
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).setJoin(SnakesProto.GameMessage.newBuilder()
                .getJoinBuilder().setPlayerName(playerName).setGameName(gameName).setRequestedRole(nodeRole)).build();
    }
    public SnakesProto.GameMessage Error(String s){
        return SnakesProto.GameMessage.newBuilder().setMsgSeq(new Random().nextLong()).setError(SnakesProto.GameMessage.newBuilder()
                .getErrorBuilder().setErrorMessage(s)).build();
    }
    public SnakesProto.GameMessage RoleChangeMsg(SnakesProto.NodeRole sender_role, SnakesProto.NodeRole receiver_role,
                                                 int sender_id, int receiver_id){
        return SnakesProto.GameMessage.newBuilder().setSenderId(sender_id).setReceiverId(receiver_id).setMsgSeq(
                new Random().nextLong()).setRoleChange(SnakesProto.GameMessage.newBuilder()
                .getRoleChangeBuilder().setSenderRole(sender_role).setReceiverRole(receiver_role)).build();
    }
}
