package com.maxifier.cliche.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.maxifier.cliche.Command;

import com.brsanthu.dataexporter.model.AlignType;
import com.brsanthu.dataexporter.model.StringColumn;
import com.brsanthu.dataexporter.output.texttable.TextTableExportOptions;
import com.brsanthu.dataexporter.output.texttable.TextTableExporter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class SshShellModuleTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(new Foo().table());
        Injector injector = Guice.createInjector(
                new SshShellModule("test", "Test App", 12890),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Foo.class).asEagerSingleton();
                    }
                });
        Thread.sleep(500000);

    }

    @CommandHandler
    public static class Foo {

        @Command
        public int add(int... operands) {
            int sum = 0;
            for (int operand : operands) {
                sum += operand;
            }
            return sum;
        }

        @Command
        public String echo(String in) {
            return in;
        }

        @Command
        public String table() {
            StringWriter sw = new StringWriter();
            TextTableExportOptions textTableExportOptions = new TextTableExportOptions();
            textTableExportOptions.setHeaderAlignment(AlignType.MIDDLE_CENTER);
            TextTableExporter exporter = new TextTableExporter(sw);
            exporter.addColumns(
                    new StringColumn("name", "NameT", 15, AlignType.MIDDLE_CENTER),
                    new StringColumn("age", "AgeT", 15, AlignType.MIDDLE_CENTER));
            exporter.startExporting();
            exporter.addBeanRows(new Bean("Aleks", 2));
            exporter.addBeanRows(new Bean("Tom", 15));
            exporter.finishExporting();
            return sw.toString();
        }
    }


    public static class Bean {

        private String name;
        private int age;

        Bean(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

}
