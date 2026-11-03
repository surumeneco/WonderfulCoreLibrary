package co.surumene.wcl.api.text;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnbtLikeParserTest {
    @Test void preservesNestedValuesAndNumericSuffixes() {
        var value = new SnbtLikeParser("{stats:{speed:12.5d,size:2L},name:'A B',flag:true}").parseCompound();
        assertEquals(Boolean.TRUE, value.get("flag"));
        assertEquals("A B", value.get("name"));
        @SuppressWarnings("unchecked")
        Map<String,Object> stats=(Map<String,Object>)value.get("stats");
        assertEquals(12.5d, ((Number)stats.get("speed")).doubleValue());
        assertEquals(2L,stats.get("size"));
    }
    @Test void rejectsMalformedInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new SnbtLikeParser("{stats:{speed:2}").parseCompound());
    }
}
