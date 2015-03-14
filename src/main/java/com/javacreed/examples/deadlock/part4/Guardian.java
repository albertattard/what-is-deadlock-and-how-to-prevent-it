/*
 * #%L
 * What is Deadlock and How to Prevent It?
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2015 Java Creed
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.javacreed.examples.deadlock.part4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.javacreed.examples.deadlock.utils.ThreadUtils;

public class Guardian implements Comparable<Guardian> {

  public static void lockAndExecute(final Runnable runnable, final Guardian... guardians) {
    Guardian.lockAndExecute(runnable, Arrays.asList(guardians));
  }

  private static void lockAndExecute(final Runnable runnable, final Iterator<Guardian> guardians) {
    if (guardians.hasNext()) {
      final Guardian guardian = guardians.next();
      ThreadUtils.log("Acquire lock: %s", guardian);
      synchronized (guardian) {
        ThreadUtils.log("Lock: %s acquired", guardian);
        Guardian.lockAndExecute(runnable, guardians);
      }
      ThreadUtils.log("Release lock: %s", guardian);
    } else {
      ThreadUtils.log("All locks are acquired.  Execute task");
      runnable.run();
    }
  }

  public static void lockAndExecute(final Runnable runnable, final List<Guardian> guardians) {
    final List<Guardian> orderedGuardians = new ArrayList<>(guardians);
    Collections.sort(orderedGuardians);
    Guardian.lockAndExecute(runnable, orderedGuardians.iterator());
  }

  private static final AtomicLong COUNTER = new AtomicLong();

  private final long index = Guardian.COUNTER.incrementAndGet();

  @Override
  public int compareTo(final Guardian other) {
    return Long.compare(index, other.index);
  }

  @Override
  public String toString() {
    return String.format("Guardian %d", index);
  }
}
