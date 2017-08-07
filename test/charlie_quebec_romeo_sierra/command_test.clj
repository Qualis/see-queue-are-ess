(ns charlie-quebec-romeo-sierra.command-test
  (:require [charlie-quebec-romeo-sierra.command
             :as command
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided throws]]))

(facts

  (defrecord-openly TestCommand []
    command/Command
    (type-of [this] ..type_of..))

  (defrecord-openly TestCommandHandler []
    command/CommandHandler
    (handle [this command] ..events..))

  (fact
    "should have command type"
    (type-of (->TestCommand)) => ..type_of..)

  (fact
    "should register handler"
    (let [handler (->TestCommandHandler)]
      (#'command/register-handler ..type_of.. handler)
      @command/handlers => {..type_of.. handler}))

  (fact
    "should find handler"
    (let [handler (->TestCommandHandler)]
      (with-redefs [command/handlers (atom {..type_of.. handler})]
        (#'command/find-handler ..type_of..) => handler)))

  (fact
    "should process command"
    (let [events '()
          command (->TestCommand)
          handler (->TestCommandHandler)]
      (command/process command) => ..events..
      (provided
        (#'command/find-handler ..type_of..) => handler)))

  (fact
    "should not process if not a 'Command'"
    (let [events '()
          handler (->TestCommandHandler)]
      (command/process ..command..) => (throws AssertionError))))
