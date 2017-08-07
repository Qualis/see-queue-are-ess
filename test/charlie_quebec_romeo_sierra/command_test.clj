(ns charlie-quebec-romeo-sierra.command-test
  (:require [charlie-quebec-romeo-sierra.command
             :as command
             :refer :all]
            [charlie-quebec-romeo-sierra.event
             :as event
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided throws]]))

(facts

  (defrecord-openly TestEvent []
    event/Event)

  (defrecord-openly TestCommand []
    command/Command
    (type-of [this] ..type_of..))

  (defrecord-openly TestCommandHandler []
    command/CommandHandler
    (handle [this command] (list (->TestEvent))))

  (facts
    "valid commands and handlers"

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
      (let [command (->TestCommand)
            handler (->TestCommandHandler)]
        (command/process command) => (list (->TestEvent))
        (provided
          (#'command/find-handler ..type_of..) => handler))))

  (facts
    "invalid commands and handlers"

    (defrecord-openly InvalidCommandHandler []
      command/CommandHandler
      (handle [this command] (list ..invalid..)))

    (fact
      "should not process if not a 'Command'"
      (let [handler (->TestCommandHandler)]
        (command/process ..command..) => (throws AssertionError)))

    (fact
      "should not return if handler returns items that are not 'Event'"
      (let [command (->TestCommand)
            handler (->InvalidCommandHandler)]
        (command/process command) => (throws AssertionError)
        (provided
          (#'command/find-handler ..type_of..) => handler)))))
