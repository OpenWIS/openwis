Facter.add(:db_initialised) do
  setcode do
  	if File.directory? '/var/lib/pgsql/data/global'
  	  'yes'
  	else
  	  'no'
  	end
  end
end
