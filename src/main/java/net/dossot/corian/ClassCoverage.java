package net.dossot.corian;

/**
 * @author David Dossot (david@dossot.net)
 */
class ClassCoverage implements Comparable<ClassCoverage> {

    private final Integer coverage;

    private final String fullPath;

    private final String toString;

    public ClassCoverage(final String fullPath, final Integer coverage) {
        this.coverage = coverage;
        this.fullPath = fullPath;

        toString =
                (coverage != null ? coverage.toString() : "? ") + "% "
                        + fullPath;
    }

    @Override
    public String toString() {
        return toString;
    }

    public int compareTo(final ClassCoverage o) {
        if (coverage == null) {
            return -1;
        }

        if (o.coverage == null) {
            return 1;
        }

        if (coverage.equals(o.coverage)) {
            return fullPath.compareTo(o.fullPath);
        }

        return coverage.compareTo(o.coverage);
    }

}
