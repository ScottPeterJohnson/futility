import net.justmachinery.futility.streams.OverrideOutputStream
import java.io.OutputStream

/**
 * An [OutputStream] that ignores calls to [close]
 */
public class NoCloseOutputStream(wrapped : OutputStream) : OverrideOutputStream(wrapped) {
    override fun close() {}
}


