/*
 * Copyright © 2015 - 2016 Lightbend, Inc. <http://www.lightbend.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.paradox.markdown

import org.scalatest.{ FlatSpec, Matchers }

class PropertiesSpec extends FlatSpec with Matchers {
  def convertPath = Path.replaceSuffix(".md", ".html")_

  val propOut = Map("out" -> "newIndex.html")
  val propOutInvalid = Map("out" -> "newIndex.foo")
  val propNoOut = Map.empty[String, String]

  "Properties.convertToTarget(properties, convertPath)(\"index.md\")" should "create target file String according to 'out' field in properties" in {
    Properties.convertToTarget(propOut, convertPath)("index.md") shouldEqual "newIndex.html"
  }

  it should "create default 'index.html' (just by replacing .md by .html) when no 'out' field is specified" in {
    Properties.convertToTarget(propNoOut, convertPath)("index.md") shouldEqual "index.html"
  }

  it should "drop the 'out' field if it is invalid (not finishing by '.html')" in {
    Properties.convertToTarget(propOutInvalid, convertPath)("index.md") shouldEqual "index.html"
  }
}