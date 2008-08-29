package net.dossot.corian;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.tmatesoft.svn.core.SVNException;
import org.xml.sax.SAXException;

/**
 * @author David Dossot (david@dossot.net)
 */
public class Main {

    private Option daysBeforeTodayOption;

    private Option svnUrlOption;

    private Option svnUserNameOption;

    private Option svnPasswordOption;

    private Option projectPathOption;

    private Option mavenProjectUrlOption;

    public static void main(final String[] args) throws Exception {
        new Main(args).run();
    }

    private Main(final String[] args) {
        parseCommandLine(args);
    }

    private void run() throws SVNException, ParserConfigurationException,
            SAXException, IOException, MalformedURLException,
            XPathExpressionException {

        final int daysBeforeToday =
                Integer.parseInt(daysBeforeTodayOption.getValue());

        final String svnUrl = svnUrlOption.getValue();
        final String svnUserName = svnUserNameOption.getValue();
        final String svnPassword = svnPasswordOption.getValue();
        final String projectPath = projectPathOption.getValue();
        final String mavenProjectUrl = mavenProjectUrlOption.getValue();

        RiskAnalyzer.analyzeRisks(daysBeforeToday, svnUrl, svnUserName,
                svnPassword, projectPath, mavenProjectUrl);
    }

    private void parseCommandLine(final String[] args) {
        final Options options = buildOptions();

        try {
            new PosixParser().parse(options, args);

        } catch (final ParseException pe) {
            new HelpFormatter().printHelp("Commit Risk Analyzer", options);
            System.exit(-1);
        }
    }

    @SuppressWarnings("static-access")
    private Options buildOptions() {
        final Options options = new Options();

        daysBeforeTodayOption =
                OptionBuilder.hasArg().withDescription(
                        "days in the past for which risk is analyzed").isRequired().create(
                        "daysBefore");

        options.addOption(daysBeforeTodayOption);

        svnUrlOption =
                OptionBuilder.hasArg().withDescription("SVN base URL").isRequired().create(
                        "svnUrl");

        options.addOption(svnUrlOption);

        svnUserNameOption =
                OptionBuilder.hasArg().withDescription("SVN user name").isRequired().create(
                        "svnUser");

        options.addOption(svnUserNameOption);

        svnPasswordOption =
                OptionBuilder.hasArg().withDescription("SVN password").isRequired().create(
                        "svnPassword");

        options.addOption(svnPasswordOption);

        projectPathOption =
                OptionBuilder.hasArg().withDescription(
                        "SVN relative project path").isRequired().create(
                        "projectPath");

        options.addOption(projectPathOption);

        mavenProjectUrlOption =
                OptionBuilder.hasArg().withDescription(
                        "Maven project site root URL").isRequired().create(
                        "projectSiteUrl");

        options.addOption(mavenProjectUrlOption);

        return options;
    }
}
