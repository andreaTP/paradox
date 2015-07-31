/*
 * Copyright © 2015 Typesafe, Inc. <http://www.typesafe.com>
 */

package com.typesafe.paradox.markdown

import com.typesafe.paradox.tree.Tree.{ Forest, Location }
import org.scalatest.{ FlatSpec, Matchers }

abstract class MarkdownBaseSpec extends FlatSpec with Matchers {

  val markdownReader = new Reader
  val markdownWriter = new Writer

  def markdown(text: String)(implicit context: Location[Page] => Writer.Context = loc => Writer.Context(loc)): Map[String, String] = {
    markdownPages("test.md" -> text)
  }

  def markdownPages(mappings: (String, String)*)(implicit context: Location[Page] => Writer.Context = loc => Writer.Context(loc)): Map[String, String] = {
    def render(location: Option[Location[Page]], rendered: Seq[(String, String)] = Seq.empty): Seq[(String, String)] = location match {
      case Some(loc) =>
        val page = loc.tree.label
        val html = normalize(markdownWriter.write(page.markdown, context(loc)))
        render(loc.next, rendered :+ (page.path, html))
      case None => rendered
    }
    render(Location.forest(pages(mappings: _*))).toMap
  }

  def pages(mappings: (String, String)*): Forest[Page] = {
    val parsed = mappings map { case (path, text) => (path, markdownReader.read(prepare(text))) }
    Page.forest(parsed, Path.replaceSuffix(Writer.DefaultSourceSuffix, Writer.DefaultTargetSuffix))
  }

  def html(text: String): Map[String, String] = {
    htmlPages("test.html" -> text)
  }

  def htmlPages(mappings: (String, String)*): Map[String, String] = {
    (mappings map { case (path, text) => (path, normalize(prepare(text))) }).toMap
  }

  def prepare(text: String): String = {
    text.stripMargin.trim
  }

  def normalize(html: String) = {
    val reader = new java.io.StringReader(html)
    val writer = new java.io.StringWriter
    val tidy = new org.w3c.tidy.Tidy
    tidy.setTabsize(2)
    tidy.setPrintBodyOnly(true)
    tidy.setShowWarnings(false)
    tidy.setQuiet(true)
    tidy.parse(reader, writer)
    writer.toString.replace("\r\n", "\n").replace("\r", "\n")
  }

}
