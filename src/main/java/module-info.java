module com.unima.risk6 {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.media;
  requires java.sql;
  requires io.netty.codec;
  requires io.netty.codec.http;
  requires io.netty.transport;
  requires io.netty.buffer;
  requires io.netty.common;
  requires io.netty.handler;
  requires java.desktop;
  requires org.xerial.sqlitejdbc;
  requires com.google.gson;
  requires org.slf4j;
  requires com.google.common;
  opens com.unima.risk6.network.message to com.google.gson;
  opens com.unima.risk6.game.logic to com.google.gson, javafx.fxml;
  exports com.unima.risk6.database.models;
  exports com.unima.risk6.gui.uimodels;
  exports com.unima.risk6.gui.scenes;
  exports com.unima.risk6.game.models.enums;
  exports com.unima.risk6.game.ai.bots;
  opens com.unima.risk6.json.jsonobjects to com.google.gson;
  opens com.unima.risk6 to javafx.fxml;
  exports com.unima.risk6;
  exports com.unima.risk6.gui.controllers;
  opens com.unima.risk6.gui.controllers to javafx.fxml;
  exports com.unima.risk6.game.models;
  opens com.unima.risk6.game.models to javafx.fxml, com.google.gson;
  exports com.unima.risk6.game.logic;
  exports com.unima.risk6.gui.controllers.enums;
  exports com.unima.risk6.game.ai.models;
  opens com.unima.risk6.gui.controllers.enums to javafx.fxml;
  exports com.unima.risk6.game.logic.controllers;
  exports com.unima.risk6.network.serialization;
  opens com.unima.risk6.network.message.enums to com.google.gson;

}