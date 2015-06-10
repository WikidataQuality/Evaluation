import requests
from collections import OrderedDict
import datetime

class averageNumberOfEditsSinceLastMonth:

	FILE_NAME = "test.txt"

	month_string_to_number = {
		'January' : 1,
		'February' : 2,
		'March' : 3,
		'April' : 4,
		'May' : 5,
		'June' : 6,
		'July' : 7,
		'August' : 8,
		'September' : 9,
		'October' : 10,
		'November' : 11,
		'December' : 12
	}

	def cut_off_what_is_no_edit(self, response):
		search_string = '<ul id=\"pagehistory\">'
		start = response.find(search_string)
		end = response.find("</ul>", start)
		return response[start+len(search_string)+1:end]

	def get_year(self, entry):
		return int(entry[-4:])

	def get_month(self, entry):
		first_whitespace = entry.find(' ')
		begin = entry.find(' ', first_whitespace+1) + 1
		end = entry.find(' ', begin)
		month = entry[begin:end]
		return int(self.month_string_to_number[month])

	def get_day(self, entry):
		return int(entry[7:9].strip())

	def get_hour(self, entry):
		if entry[0] == 0:
			return int(entry[1])
		else:
			return int(entry[:1])

	def get_minute(self, entry):
		if entry[3] == 0:
			return int(entry[4])
		else:
			return int(entry[3:4])


	def cut_off_what_is_no_date(self, entry):
		search_string = 'class=\"mw-changeslist-date\">'
		begin = entry.find(search_string) + len(search_string)
		end = entry.find('</a>', begin)
		return entry[begin:end]

	def parse_date(self, entry):
		entry = self.cut_off_what_is_no_date(entry)
		date = {}
		date['year'] = self.get_year(entry)
		date['month'] = self.get_month(entry)
		date['day'] = self.get_day(entry)
		date['hour'] = self.get_hour(entry)
		date['minute'] = self.get_minute(entry)
		return date

	def is_in_time(self, entry, date):
		date_of_entry = self.parse_date(entry)
		if date_of_entry['year'] > date['year']:
			return True
		elif date_of_entry['year'] == date['year']:
			if date_of_entry['month'] > date['month']:
				return True
			elif date_of_entry['month'] == date['month']:
				if date_of_entry['day'] > date['day']:
					return True
				elif date_of_entry['day'] == date['day']:
					if date_of_entry['hour'] > date['hour']:
						return True
					elif date_of_entry['hour'] == date['hour']:
						if date_of_entry['minute'] >= date['minute']:
							return True
		else:
			return False

	def is_valid_entry(self, entry):
		return entry.find("<span class=\"mw-history-histlinks\">") != -1

	def is_valid_entry_in_time(self, entry, date):
		if self.is_valid_entry(entry) and self.is_in_time(entry, date):
			return True

		return False

	def get_amount_of_edits_since(self, date, response):
		response = self.cut_off_what_is_no_edit(response)
		response = response.split("\n")
		edits = 0
		for line in response:
			if self.is_valid_entry_in_time(line, date):
				edits += 1
			else:
				break

		return edits

	def one_month_ago(self):
		now = datetime.datetime.now()
		date = {}
		if now.month == '1':
			date['year'] = now.year - 1
			date['month'] = 1
		else:
			date['year'] = now.year
			date['month'] = now.month - 1
		date['day'] = now.day
		date['hour'] = now.hour
		date['minute'] = now.minute
		return date


	def run(self):
		date = self.one_month_ago()
		total_items = 0
		total_edits = 0
		with open("averageNumberOfEditsInLastMonth.csv", "w") as f:
			for i in range(1,100001):
				entity = "Q" + str(i)
				response = requests.get("http://www.wikidata.org/w/index.php?title=" + entity + "&offset=&limit=500&action=history").text
				if response.find("There is no edit history for this page.") != -1:
					continue
				edits = self.get_amount_of_edits_since(date, response)
				total_edits += edits
				total_items += 1
				print(entity, edits)
				f.write(entity + ';' + str(edits) + '\n')
				f.flush()

			print("Total items: " + str(total_items) + "\nTotal edits: " + str(total_edits) + "\nAverage number of edits since last month: " + str(float(total_edits) / total_items))
			f.write(str(float(total_edits) / total_items))

def main():
	counter = averageNumberOfEditsSinceLastMonth()
	counter.run()

if __name__ == "__main__": main()