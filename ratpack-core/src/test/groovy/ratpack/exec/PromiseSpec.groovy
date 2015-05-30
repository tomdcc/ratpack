/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.exec

import ratpack.func.Function
import ratpack.test.exec.ExecHarness
import spock.lang.Specification

class PromiseSpec extends Specification {

  def exec = ExecHarness.harness()

  def "cannot subscribe to promise when blocking"() {
    when:
    exec.yield {
      exec.blocking {
        exec.promiseOf(1).then { it }
      }
    }.valueOrThrow

    then:
    def e = thrown ExecutionException
    e.message.startsWith("Promise.then() can only be called on a compute thread")
  }

  def "retry"() {
    given:
    ratpack.func.Factory factory = Mock(ratpack.func.Factory)
    1 * factory.create() >> { throw new IllegalArgumentException() }
    1 * factory.create() >> { throw new IllegalArgumentException() }
    1 * factory.create() >> { 'hi' }

    when:
    def result = exec.yield {
      exec.promiseFrom(factory).retry(5)
    }

    then:
    result.value == 'hi'
  }
}
