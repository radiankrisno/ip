package processor;

import config.Setting;
import exception.DukeException;
import models.Command;
import models.Deadline;
import models.Event;
import models.TaskList;
import models.Todo;
import util.Output;

import java.util.List;


import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.List;


public class Processor implements IProcessor {

    private TaskList list = new TaskList(Setting.FILE_DIRECTORY, Setting.FILE_NAME);

    @Override
    public void processCommand(Command command, List<String> arguments) {
        switch(command) {
            case BYE:
                processBye();
                break;
            case LIST:
                processList();
                break;
            case DONE:
                processDone(arguments.get(1));
                break;
            case DELETE:
                processDelete(arguments.get(1));
                break;
            default:
                processDefault(arguments);
        }
    }

    @Override
    public void processDefault(List<String> arguments) {
        try {
            String type = arguments.get(0);
            if (type.equals("todo")) {
                arguments.remove(0);
                if (arguments.size() == 0) {
                    throw new DukeException("Todo description cannot be empty");
                }
                this.list.addTask(new Todo(String.join(" ", arguments)));
            } else if (type.equals("deadline")) {
                arguments.remove(0);
                if (arguments.size() == 0) {
                    throw new DukeException("Deadline description cannot be empty");
                }
                String line = String.join(" ", arguments);
                String[] input = line.split(" /by ");
                if (input.length == 1) {
                    throw new DukeException("Deadline command must have /by specified");
                }
                LocalDate time = LocalDate.parse(input[1].trim());
                this.list.addTask(new Deadline(input[0], time));
            } else if (type.equals("event")) {
                arguments.remove(0);
                if (arguments.size() == 0) {
                    throw new DukeException("Event description cannot be empty");
                }
                String line = String.join(" ", arguments);
                String[] input = line.split(" /at ");
                if (input.length == 1) {
                    throw new DukeException("Event command must have /at specified");
                }
                LocalDate time = LocalDate.parse(input[1].trim());
                this.list.addTask(new Event(input[0], time));
            } else {
                throw new DukeException("I don't understand:(");
            }
            Output.print("Got it. I've added this task:\n   " + this.list.getLastTask() + "\nNow you have " + this.list.getSize() + " tasks in the list.");
        } catch (DukeException e) {
            Output.print(e.getMessage());
        } catch (DateTimeParseException e) {
            Output.print(e.getMessage());
        }
    }

    @Override
    public void processList() {
        Output.print(this.list.toString());
    }

    @Override
    public void processDone(String index) {
        try {
            int i = Integer.parseInt(index);
            this.list.setDone(i - 1);
            Output.print("Nice! I've marked this task as done:\n   " + this.list.getTask(i - 1));
        } catch(DukeException e) {
            Output.print(e.getMessage());
        }
    }

    @Override
    public void processDelete(String index) {
        try {
            int i = Integer.parseInt(index);
            String result = this.list.deleteTask(i - 1);
            Output.print("Got it! I've removed this task:\n   " + result + "\nNow you have " + this.list.getSize() + " tasks in the list.");
        } catch (DukeException e) {
            Output.print(e.getMessage());
        }
    }

    @Override
    public void processBye() {
        Output.print("Bye. Please meet me again later!");
    }
}