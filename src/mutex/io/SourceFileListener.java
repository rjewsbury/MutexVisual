package mutex.io;

public interface SourceFileListener
{
	void loadFile(SourceFile file);
	void saveFile(SourceFile file);
}
