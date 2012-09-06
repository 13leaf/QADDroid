use warnings;

my $parent="src/com/qad/app";
my %bases = (
"ListActivity" => {
	package => "android.app"
	},
"ActivityGroup" => {
	package => "android.app"
	},
"PreferenceActivity" =>{
	package => "android.preference"	
	},
"FragmentActivity" => {
	package=>"android.support.v4.app"
	}
);
open( $in, "<", "$parent/BaseActivity.java" ) or die "Open error $!";
my @lines = <$in>;
close ($in) or die "close error $!";
my $out;
foreach $genClass (keys %bases) {
	open ($out,">","$parent/Base$genClass.java") or die "Open error $!";
	my @copy=@lines;
	my $import="$bases{$genClass}->{'package'}.$genClass";
	foreach my $line (@copy) {
		#插入import
		if ( $line =~ m/^import android.app.(\w*?)Activity;$/ ) {
			print $out "import $import;\n";
		}
		$line =~ s/^public class (\w+?) extends (\w+?){$/public class Base$genClass extends $genClass {\n/;
		$line =~ s/protected Activity me;/protected Base$genClass me;/g;
		print $out $line;
	}
	close ($out) or die "close error $!";
}