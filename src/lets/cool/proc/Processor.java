package lets.cool.proc;

public interface Processor<IN, OUT> {
    OUT process(IN data);
}
