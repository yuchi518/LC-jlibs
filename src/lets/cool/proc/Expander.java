package lets.cool.proc;

import java.util.Iterator;

public interface Expander<IN, OUT extends Iterator<?>> extends Processor<IN, OUT> {
}
