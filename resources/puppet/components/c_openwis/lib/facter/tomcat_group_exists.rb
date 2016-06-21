Facter.add(:tomcat_group_exists) do
  setcode do
    Facter::Util::Resolution.exec('grep tomcat /etc/group')
  end
end
