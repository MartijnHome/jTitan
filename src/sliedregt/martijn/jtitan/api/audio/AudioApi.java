package sliedregt.martijn.jtitan.api.audio;

import sliedregt.martijn.jtitan.api.Api;

public abstract class AudioApi extends Api
{

	protected AudioApi(final String name, final long version)
	{
		super(Api.TYPE_AUDIO, name, version);
	}

}
