package test;

import java.util.Map;

import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.engine.ExpansionFrame;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.TagExtensionBase;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.TagExtensionGroup;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.nodes.EngineRtData;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtNodeList;
import org.sweble.wikitext.parser.nodes.WtTagExtension;
import org.sweble.wikitext.parser.nodes.WtTagExtensionBody;

@SuppressWarnings("serial")
final class TranslateTagExtensionGroup
		extends
			TagExtensionGroup
{
	public TranslateTagExtensionGroup(WikiConfig wikiConfig)
	{
		super(TranslateTagExtensionGroup.class.getSimpleName());
		addTagExtension(new TagExtensionTranslate(wikiConfig));
	}

	public static final class TagExtensionTranslate
			extends
				TagExtensionBase
	{
		private static final long serialVersionUID = 1L;

		private static final String NAME = "translate";

		/** For un-marshaling only. */
		public TagExtensionTranslate()
		{
			super(NAME);
		}

		public TagExtensionTranslate(WikiConfig wikiConfig)
		{
			super(wikiConfig, NAME);
		}

		@Override
		public WtNode invoke(
				ExpansionFrame frame,
				WtTagExtension tagExt,
				Map<String, WtNodeList> attrs,
				WtTagExtensionBody body)
		{
			WtEngineImpl engine = frame.getEngine();
			try
			{
				// TODO: parse also does validation which is superfluous since
				// the input has already been validated.

				// TODO: the expansion is performed in a new frame, not in the
				// current frame. Dependeing on the semantics of <translate> the
				// expansion has to be done in the current frame.

				EngProcessedPage parsed = engine.parse(
						// TODO: passing a PageId does not really make sense...
						new PageId(frame.getTitle(), -1),
						body.getContent(),
						frame.getCallback());

				WtNodeList extracted = nf().list();
				extracted.addAll(parsed.getPage());
				return extracted;
			}
			catch (EngineException e)
			{
				return EngineRtData.set(nf().softError(tagExt));
			}
		}
	}
}