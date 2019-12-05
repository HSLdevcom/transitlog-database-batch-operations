//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
// Tuned by @author akivv
//

package fi.hsl.common.batch;

import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface LongRowMapper<T> {
    @Nullable
    T mapRow(ResultSet var1, long var2) throws SQLException;
}
