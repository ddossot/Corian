package net.dossot.corian;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author David Dossot (david@dossot.net)
 */
public abstract class RiskAnalyzer {

    private RiskAnalyzer() {
        throw new UnsupportedOperationException();
    }

    static void analyzeRisks(final int daysBeforeToday, final String svnUrl,
            final String svnUserName, final String svnPassword,
            final String projectPath, final String mavenProjectUrl)
            throws SVNException, ParserConfigurationException, SAXException,
            IOException, MalformedURLException, XPathExpressionException {

        final Set<String> changedPaths =
                getChangedPathsFromSvn(daysBeforeToday, svnUrl, svnUserName,
                        svnPassword, projectPath);

        final SortedSet<ClassCoverage> classCoverages =
                getClassCoverageFromRevisions(projectPath, mavenProjectUrl,
                        changedPaths);

        for (final ClassCoverage classCoverage : classCoverages) {
            System.out.println(classCoverage);
        }
    }

    private static SortedSet<ClassCoverage> getClassCoverageFromRevisions(
            final String projectPath, final String mavenProjectUrl,
            final Set<String> changedPaths)
            throws ParserConfigurationException, SAXException, IOException,
            MalformedURLException, XPathExpressionException {

        final SortedSet<ClassCoverage> classCoverages =
                new TreeSet<ClassCoverage>();

        final Map<String, Document> cachedCoverageSummaries =
                new HashMap<String, Document>();

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(false);
        final DocumentBuilder builder = dbf.newDocumentBuilder();

        for (final String changedPath : changedPaths) {
            final String module = getModuleFromPath(projectPath, changedPath);

            Document coverageSummary = cachedCoverageSummaries.get(module);

            if (coverageSummary == null) {
                final String coverageSummaryUrl =
                        mavenProjectUrl + "/" + module
                                + "/cobertura/frame-sourcefiles.html";

                coverageSummary =
                        builder.parse(new URL(coverageSummaryUrl).openStream());

                cachedCoverageSummaries.put(module, coverageSummary);
            }

            final String fqn = getFqnFromPath(changedPath);

            classCoverages.add(new ClassCoverage(changedPath,
                    getCoverageForFqn(coverageSummary, fqn)));

        }

        return classCoverages;
    }

    private static Set<String> getChangedPathsFromSvn(
            final int daysBeforeToday, final String svnUrl,
            final String svnUserName, final String svnPassword,
            final String projectPath) throws SVNException {

        DAVRepositoryFactory.setup();

        final SVNRepository repository =
                SVNRepositoryFactory.create(SVNURL.parseURIDecoded(svnUrl));

        final ISVNAuthenticationManager authManager =
                SVNWCUtil.createDefaultAuthenticationManager(svnUserName,
                        svnPassword);

        repository.setAuthenticationManager(authManager);

        final Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -daysBeforeToday);

        final long startRevision =
                repository.getDatedRevision(startDate.getTime());

        System.out.printf("Revision on %tc: %d\n", startDate, startRevision);

        final long endRevision = repository.getLatestRevision();
        System.out.printf("Latest revision: %d\n\n", endRevision);

        final Set<String> changedPaths = new HashSet<String>();

        repository.log(new String[] { projectPath }, startRevision,
                endRevision, true, true, new ISVNLogEntryHandler() {

                    @SuppressWarnings("unchecked")
                    public void handleLogEntry(final SVNLogEntry logEntry)
                            throws SVNException {

                        for (final String path : (Set<String>) logEntry.getChangedPaths().keySet()) {
                            if (acceptPath(path)) {
                                changedPaths.add(path);
                            }
                        }
                    }

                    private boolean acceptPath(final String path) {
                        return path.endsWith(".java")
                                && path.toLowerCase(Locale.getDefault()).indexOf(
                                        "test") == -1;
                    }
                });

        repository.closeSession();
        return changedPaths;
    }

    private static Integer getCoverageForFqn(final Document coverageSummary,
            final String fqn) throws XPathExpressionException {

        final String rawCoverage =
                StringUtils.remove(StringUtils.substringBetween(
                        XPathFactory.newInstance().newXPath().evaluate(
                                "//td[a[@href='" + fqn + ".html']]/i/text()",
                                coverageSummary), "(", ")"), '%');

        if (StringUtils.isBlank(rawCoverage)
                || "N/A".equalsIgnoreCase(rawCoverage)) {
            return null;
        }

        return Integer.valueOf(rawCoverage);
    }

    private static String getFqnFromPath(final String changedPath) {
        return StringUtils.replaceChars(StringUtils.substringBetween(
                changedPath, "src/main/java/", ".java"), "/", ".");
    }

    private static String getModuleFromPath(final String projectPath,
            final String changedPath) {

        return StringUtils.substringBetween(changedPath, projectPath,
                "/src/main/java/").substring(1);
    }

}
