/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package lets.cool.util;

import lets.cool.util.iter.SingleIterator;

import java.util.Iterator;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Semaphore;
import java.util.function.*;
import java.util.stream.Stream;

/**
 *
 * @param <IN> Object type maintained by upper pipeline
 * @param <OUT> Object type maintained by current pipeline
 */
public abstract class Pipeline<IN, OUT> {

    public static <T> Pipeline<T, T> withData(Iterator<T> iterator) {
        Pipeline<T, T> pipeline = new Pipeline<T, T>() {

            @Override
            protected void processIt(T in) {
                if (nextPipeline != null) {
                    nextPipeline.queueIt(in);
                }
            }

            @Override
            public boolean execute() {
                while(iterator.hasNext()) {
                    T t = iterator.next();
                    this.queueIt(t);
                }
                return true;
            }

        };
        return pipeline;
    }

    public static <T> Pipeline<T, T> withData(Iterable<T> iterable) {
        return Pipeline.withData(iterable.iterator());
    }

    public static <T> Pipeline<T, T> withData(Stream<T> stream) {
        return Pipeline.withData(stream.iterator());
    }

    public static <T> Pipeline<T, T> withData(T t) {
        return Pipeline.withData(new SingleIterator<>(t));
    }

    protected int numOfConcurrentWorks = 1;
    protected String nameOfExecutorService = ExecutorServices.PIPELINE;
    protected Pipeline upperPipeline = null, nextPipeline = null;
    protected Semaphore semaphore = new Semaphore(numOfConcurrentWorks, false);
    protected CompletionService<Void> completionService = ExecutorServices.cs(nameOfExecutorService);

    public Pipeline<IN, OUT> withConcurrentWorks(int num) {
        numOfConcurrentWorks = (num<=0) ? Runtime.getRuntime().availableProcessors(): num;
        semaphore = new Semaphore(numOfConcurrentWorks, false);
        return this;
    }

    public Pipeline<IN, OUT> withMaxNumberConcurrentWorks() {
        return withConcurrentWorks(Runtime.getRuntime().availableProcessors());
    }

    public Pipeline<IN, OUT> withNameOfExecutorService(String name) {
        nameOfExecutorService = name == null ? ExecutorServices.PIPELINE: name;
        completionService = ExecutorServices.cs(nameOfExecutorService);
        return this;
    }

    public Pipeline<OUT, OUT> each(Consumer<OUT> operator) {
        Pipeline<OUT, OUT> newPipeline = new Pipeline<OUT, OUT>() {
            @Override
            protected void processIt(OUT in) {
                operator.accept(in);
                if (nextPipeline != null) {
                    nextPipeline.queueIt(in);
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }


    public <REF> Pipeline<OUT, OUT> each(BiConsumer<OUT, REF> operator, REF ref) {
        Pipeline<OUT, OUT> newPipeline = new Pipeline<OUT, OUT>() {
            @Override
            protected void processIt(OUT in) {
                operator.accept(in, ref);
                if (nextPipeline != null) {
                    nextPipeline.queueIt(in);
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public Pipeline<OUT, OUT> filter(Predicate<? super OUT> predicate) {
        Pipeline<OUT, OUT> newPipeline = new Pipeline<OUT, OUT>() {
            @Override
            protected void processIt(OUT in) {
                boolean b = predicate.test(in);
                if (b && nextPipeline != null) {
                    nextPipeline.queueIt(in);
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public <REF> Pipeline<OUT, OUT> filter(BiPredicate<? super OUT, REF> predicate, REF ref) {
        Pipeline<OUT, OUT> newPipeline = new Pipeline<OUT, OUT>() {
            @Override
            protected void processIt(OUT in) {
                boolean b = predicate.test(in, ref);
                if (b && nextPipeline != null) {
                    nextPipeline.queueIt(in);
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public <R> Pipeline<OUT, R> map(Function<? super OUT, ? extends R> mapper) {
        Pipeline<OUT, R> newPipeline = new Pipeline<OUT, R>() {

            @Override
            protected void processIt(OUT in) {
                R r = mapper.apply(in);
                if (nextPipeline != null) {
                    nextPipeline.queueIt(r);
                }
            }

        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public <R, REF> Pipeline<OUT, R> map(BiFunction<? super OUT, REF, ? extends R> mapper, REF ref) {
        Pipeline<OUT, R> newPipeline = new Pipeline<OUT, R>() {

            @Override
            protected void processIt(OUT in) {
                R r = mapper.apply(in, ref);
                if (nextPipeline != null) {
                    nextPipeline.queueIt(r);
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public <R> Pipeline<OUT, R> expand(Function<? super OUT, ? extends Iterator<R>> expander) {
        Pipeline<OUT, R> newPipeline = new Pipeline<OUT, R>() {

            @Override
            protected void processIt(OUT in) {
                Iterator<R> rs = expander.apply(in);
                if (nextPipeline != null) {
                    if (rs!= null) {
                        while (rs.hasNext()) {
                            R r = rs.next();
                            nextPipeline.queueIt(r);
                        }
                    }
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public <R, REF> Pipeline<OUT, R> expand(BiFunction<? super OUT, REF, ? extends Iterator<R>> expander, REF ref) {
        Pipeline<OUT, R> newPipeline = new Pipeline<OUT, R>() {

            @Override
            protected void processIt(OUT in) {
                Iterator<R> rs = expander.apply(in, ref);
                if (nextPipeline != null) {
                    if (rs!= null) {
                        while (rs.hasNext()) {
                            R r = rs.next();
                            nextPipeline.queueIt(r);
                        }
                    }
                }
            }
        };

        this.nextPipeline = newPipeline;
        newPipeline.upperPipeline = this;

        return newPipeline;
    }

    public boolean execute() {
        if (upperPipeline != null) {
            if (!upperPipeline.execute()) return false;
            try {
                while (upperPipeline.semaphore.hasQueuedThreads()) {
                    // wait to done
                    upperPipeline.completionService.take();
                }
                upperPipeline.semaphore.acquire(upperPipeline.numOfConcurrentWorks);

                if (nextPipeline == null) {
                    // last one
                    while (semaphore.hasQueuedThreads()) {
                        // wait to done
                        completionService.take();
                    }
                    semaphore.acquire(numOfConcurrentWorks);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // This function will be replaced for first object
            throw new UnsupportedOperationException("This should be implemented by first object.");
        }
        return true;
    }

    protected abstract void processIt(IN in);

    /**
     * Upper pipeline call this to trigger next pipeline.
     * @param obj
     */
    @SuppressWarnings("unchecked")
    protected void queueIt(Object obj) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        completionService.submit(() -> {
            Exception exception = null;
            try {
                processIt((IN) obj);
            } catch (Exception ex) {
                ex.printStackTrace();
                exception = ex;
            } finally {
                semaphore.release();
            }

            if (exception != null)
                throw new RuntimeException(exception);

            return null;
        });

    }
}
